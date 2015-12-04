package pl.uservices.aggregatr.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Trace;
import pl.uservices.aggregatr.aggregation.model.IngredientType;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Version;

@Slf4j
class DojrzewatrUpdater {

    private final IngredientsProperties ingredientsProperties;
    private final IngredientWarehouse ingredientWarehouse;
    private final MaturingServiceClient maturingServiceClient;

    public DojrzewatrUpdater(IngredientsProperties ingredientsProperties,
                             IngredientWarehouse ingredientWarehouse,
                             MaturingServiceClient maturingServiceClient) {
        this.ingredientsProperties = ingredientsProperties;
        this.ingredientWarehouse = ingredientWarehouse;
        this.maturingServiceClient = maturingServiceClient;
    }

    Ingredients updateIfLimitReached(Ingredients ingredients, String processId) {
        if (ingredientsMatchTheThreshold(ingredients)) {
            log.info("Ingredients match the threshold - time to notify dojrzewatr!");
            notifyDojrzewatr(ingredients, processId);
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

    private void notifyDojrzewatr(Ingredients ingredients, String processId) {
        maturingServiceClient.distributeIngredients(ingredients, processId);
    }
}
