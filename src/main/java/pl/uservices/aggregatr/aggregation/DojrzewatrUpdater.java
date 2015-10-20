package pl.uservices.aggregatr.aggregation;

import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.TraceScope;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import pl.uservices.aggregatr.aggregation.model.IngredientType;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Version;

@Slf4j
class DojrzewatrUpdater {

    private final ServiceRestClient serviceRestClient;
    private final RetryExecutor retryExecutor;
    private final IngredientsProperties ingredientsProperties;
    private final IngredientWarehouse ingredientWarehouse;
    private final Trace trace;

    public DojrzewatrUpdater(ServiceRestClient serviceRestClient, RetryExecutor retryExecutor,
                             IngredientsProperties ingredientsProperties, IngredientWarehouse ingredientWarehouse, Trace trace) {
        this.serviceRestClient = serviceRestClient;
        this.retryExecutor = retryExecutor;
        this.ingredientsProperties = ingredientsProperties;
        this.ingredientWarehouse = ingredientWarehouse;
        this.trace = trace;
    }

    Ingredients updateIfLimitReached(Ingredients ingredients) {
        if (ingredientsMatchTheThreshold(ingredients)) {
            log.info("Ingredients match the threshold - time to notify dojrzewatr!");
            notifyDojrzewatr(ingredients);
            ingredientWarehouse.useIngredients(ingredientsProperties.getThreshold());
        }
        Ingredients currentState = ingredientWarehouse.getCurrentState();
        log.info("Current state of ingredients is {}", currentState);
        return currentState;
    }

    private boolean ingredientsMatchTheThreshold(Ingredients ingredients) {
        boolean allIngredientsPresent = ingredients.ingredients.size() == IngredientType.values().length;
        boolean allIngredientsOverThreshold =
                ingredients.ingredients.stream().allMatch(
                        ingredient -> ingredient.getQuantity() >= ingredientsProperties.getThreshold());
        return allIngredientsPresent && allIngredientsOverThreshold;
    }

    private void notifyDojrzewatr(Ingredients ingredients) {
        TraceScope scope = this.trace.startSpan("calling_dojrzewatr", new AlwaysSampler(), null);
        serviceRestClient.forService("dojrzewatr")
                .post()
                .onUrl("/brew")
                .body(ingredients)
                .withHeaders().contentType(Version.DOJRZEWATR_V1)
                .andExecuteFor()
                .ignoringResponse();
        scope.close();
    }
}
