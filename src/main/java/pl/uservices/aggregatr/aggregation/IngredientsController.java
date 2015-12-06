package pl.uservices.aggregatr.aggregation;

import static pl.uservices.aggregatr.aggregation.TestConfigurationHolder.CURRENT_HOLDER;
import static pl.uservices.aggregatr.aggregation.TestConfigurationHolder.TEST_COMMUNICATION_TYPE_HEADER_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pl.uservices.aggregatr.aggregation.TestConfigurationHolder.TestCommunicationType;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Order;
import pl.uservices.aggregatr.aggregation.model.Version;

@RestController
@RequestMapping(value = "/ingredients", consumes = Version.AGGREGATING_V1, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class IngredientsController {

    private final IngredientsAggregator ingredientsAggregator;

    @Autowired
    public IngredientsController(IngredientsAggregator ingredientsAggregator) {
        this.ingredientsAggregator = ingredientsAggregator;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Ingredients distributeIngredients(@RequestBody Order order,
                                             @RequestHeader("PROCESS-ID") String processId,
                                             @RequestHeader(value = TEST_COMMUNICATION_TYPE_HEADER_NAME,
                                                     defaultValue = "REST_TEMPLATE", required = false)
                                             TestCommunicationType testCommunicationType) {
        CURRENT_HOLDER.set(TestConfigurationHolder.builder().testCommunicationType(testCommunicationType).build());
        log.info("Starting process for process id [{}]", processId);
        return ingredientsAggregator.fetchIngredients(order, processId);
    }

}
