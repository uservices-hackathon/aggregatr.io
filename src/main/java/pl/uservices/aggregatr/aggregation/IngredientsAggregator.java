package pl.uservices.aggregatr.aggregation;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.netflix.hystrix.HystrixCommandKey;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.stereotype.Component;
import pl.uservices.aggregatr.aggregation.model.Ingredient;
import pl.uservices.aggregatr.aggregation.model.IngredientType;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Order;

import java.util.List;
import java.util.stream.Collectors;

import static com.netflix.hystrix.HystrixCommand.Setter.withGroupKey;
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;

@Slf4j
@Component
class IngredientsAggregator {

    private final IngredientsProperties ingredientsProperties;
    private final DojrzewatrUpdater dojrzewatrUpdater;
    private final ServiceRestClient serviceRestClient;
    private final RetryExecutor retryExecutor;
    private final IngredientWarehouse ingredientWarehouse;

    @Autowired
    IngredientsAggregator(ServiceRestClient serviceRestClient,
                          RetryExecutor retryExecutor,
                          IngredientsProperties ingredientsProperties,
                          MetricRegistry metricRegistry, IngredientWarehouse ingredientWarehouse, Trace trace) {
        this.serviceRestClient = serviceRestClient;
        this.retryExecutor = retryExecutor;
        this.ingredientWarehouse = ingredientWarehouse;
        this.dojrzewatrUpdater = new DojrzewatrUpdater(serviceRestClient, retryExecutor, ingredientsProperties,
                ingredientWarehouse, trace);
        this.ingredientsProperties = ingredientsProperties;
        setupMeters(metricRegistry);
    }

    private void setupMeters(MetricRegistry metricRegistry) {
        metricRegistry.register(getMetricName(IngredientType.WATER),
                (Gauge<Integer>) () -> ingredientWarehouse.getIngredientCountOfType(IngredientType.WATER));
        metricRegistry.register(getMetricName(IngredientType.HOP),
                (Gauge<Integer>) () -> ingredientWarehouse.getIngredientCountOfType(IngredientType.HOP));
        metricRegistry.register(getMetricName(IngredientType.MALT),
                (Gauge<Integer>) () -> ingredientWarehouse.getIngredientCountOfType(IngredientType.MALT));
        metricRegistry.register(getMetricName(IngredientType.YIEST),
                (Gauge<Integer>) () -> ingredientWarehouse.getIngredientCountOfType(IngredientType.YIEST));
    }

    private String getMetricName(IngredientType ingredientType) {
        return "ingredients." + ingredientType.toString().toLowerCase();
    }

    Ingredients fetchIngredients(Order order) {
        List<ListenableFuture<Ingredient>> futures = ingredientsProperties
                .getListOfServiceNames(order)
                .stream()
                .map(this::harvest)
                .collect(Collectors.toList());
        ListenableFuture<List<Ingredient>> allDoneFutures = Futures.allAsList(futures);
        List<Ingredient> allIngredients = Futures.getUnchecked(allDoneFutures);
        allIngredients.stream()
                .filter(ingredient -> ingredient != null)
                .forEach(ingredientWarehouse::addIngredient);
        Ingredients ingredients = ingredientWarehouse.getCurrentState();
        return dojrzewatrUpdater.updateIfLimitReached(ingredients);
    }

    ListenableFuture<Ingredient> harvest(String service) {
        return serviceRestClient.forExternalService()
                .retryUsing(retryExecutor)
                .get()
                .withCircuitBreaker(withGroupKey(asKey(service)).andCommandKey(HystrixCommandKey.Factory.asKey(service + "_command")), () -> {
                    log.error("Can't connect to {}", service);
                    return null;
                })
                .onUrl(ingredientsProperties.getRootUrl() + "/" + service)
                .andExecuteFor()
                .anObject()
                .ofTypeAsync(Ingredient.class);
    }
}
