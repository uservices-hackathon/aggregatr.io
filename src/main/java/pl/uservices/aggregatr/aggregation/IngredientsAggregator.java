package pl.uservices.aggregatr.aggregation;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.sleuth.TraceManager;
import org.springframework.cloud.sleuth.instrument.hystrix.TraceCommand;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import com.netflix.hystrix.HystrixCommandKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import pl.uservices.aggregatr.aggregation.model.Ingredient;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Order;

import static com.netflix.hystrix.HystrixCommand.Setter.withGroupKey;
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;

@Slf4j
@Component
class IngredientsAggregator {

    private final IngredientsProperties ingredientsProperties;
    private final MaturingServiceUpdater dojrzewatrUpdater;
    private final IngredientWarehouse ingredientWarehouse;
    private final AsyncRestTemplate asyncRestTemplate;
    private final TraceManager traceManager;

    @Autowired
    IngredientsAggregator(IngredientsProperties ingredientsProperties,
                          IngredientWarehouse ingredientWarehouse,
                          TraceManager traceManager, AsyncRestTemplate asyncRestTemplate,
                          MaturingServiceClient maturingServiceClient, @LoadBalanced RestTemplate restTemplate) {
        this.ingredientWarehouse = ingredientWarehouse;
        this.asyncRestTemplate = asyncRestTemplate;
        this.traceManager = traceManager;
        this.dojrzewatrUpdater = new MaturingServiceUpdater(ingredientsProperties,
                ingredientWarehouse, maturingServiceClient, restTemplate);
        this.ingredientsProperties = ingredientsProperties;
    }

    Ingredients fetchIngredients(Order order, String processId) {
        List<ListenableFuture<ResponseEntity<Ingredient>>> futures = ingredientsProperties
                .getListOfServiceNames(order)
                .stream()
                .map(this::harvest)
                .collect(Collectors.toList());
        List<Ingredient> allIngredients = futures.stream()
                .map(this::getUnchecked)
                .map(HttpEntity::getBody)
                .collect(Collectors.toList());
        allIngredients.stream()
                .filter(ingredient -> ingredient != null)
                .forEach(ingredientWarehouse::addIngredient);
        Ingredients ingredients = ingredientWarehouse.getCurrentState();
        return dojrzewatrUpdater.updateIfLimitReached(ingredients, processId);
    }

    private <T> T getUnchecked(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("Exception occurred while trying to get the future", e);
        }
        return null;
    }

    ListenableFuture<ResponseEntity<Ingredient>> harvest(String service) {
        TraceCommand<ListenableFuture<ResponseEntity<Ingredient>>> traceCommand = new TraceCommand<ListenableFuture<ResponseEntity<Ingredient>>>(traceManager,
                withGroupKey(asKey(service)).andCommandKey(HystrixCommandKey.Factory.asKey(service + "_command"))) {
            @Override
            public ListenableFuture<ResponseEntity<Ingredient>> doRun() throws Exception {
                return asyncRestTemplate.getForEntity(ingredientsProperties.getRootUrl() + "/" + service,
                        Ingredient.class);
            }

            @Override
            protected ListenableFuture<ResponseEntity<Ingredient>> getFallback() {
                log.error("Can't connect to {}", service);
                return null;
            }
        };
        try {
            return traceCommand.doRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
