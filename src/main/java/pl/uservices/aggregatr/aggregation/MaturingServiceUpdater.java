package pl.uservices.aggregatr.aggregation;

import static pl.uservices.aggregatr.aggregation.TestConfigurationHolder.CURRENT_HOLDER;
import static pl.uservices.aggregatr.aggregation.TestConfigurationHolder.TEST_COMMUNICATION_TYPE_HEADER_NAME;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import pl.uservices.aggregatr.aggregation.model.IngredientType;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Version;

@Slf4j
class MaturingServiceUpdater {

    private final IngredientsProperties ingredientsProperties;
    private final IngredientWarehouse ingredientWarehouse;
    private final MaturingServiceClient maturingServiceClient;
    private final RestTemplate restTemplate;

    public MaturingServiceUpdater(IngredientsProperties ingredientsProperties,
                                  IngredientWarehouse ingredientWarehouse,
                                  MaturingServiceClient maturingServiceClient, RestTemplate restTemplate) {
        this.ingredientsProperties = ingredientsProperties;
        this.ingredientWarehouse = ingredientWarehouse;
        this.maturingServiceClient = maturingServiceClient;
        this.restTemplate = restTemplate;
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
        switch (CURRENT_HOLDER.get().getTestCommunicationType()) {
            case FEIGN:
                callViaFeign(ingredients, processId);
                break;
            default:
                useRestTemplateToCallAggregation(ingredients, processId);
        }
    }

    private void callViaFeign(Ingredients ingredients, String processId) {
        maturingServiceClient.distributeIngredients(ingredients, processId);
    }

    private void useRestTemplateToCallAggregation(Ingredients body, String processId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PROCESS-ID", processId);
        headers.add(TEST_COMMUNICATION_TYPE_HEADER_NAME, TestConfigurationHolder.CURRENT_HOLDER.get().getTestCommunicationType().name());
        headers.add("Content-Type", Version.MATURING_V1);
        String serviceName = "maturing";
        String url = "brew";
        URI uri = URI.create("http://" + serviceName + "/" + url);
        HttpMethod method = HttpMethod.POST;
        RequestEntity<Ingredients> requestEntity = new RequestEntity<>(body, headers, method, uri);
        restTemplate.exchange(requestEntity, String.class);
    }
}
