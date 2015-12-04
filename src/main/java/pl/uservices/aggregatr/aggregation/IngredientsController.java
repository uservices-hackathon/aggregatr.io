package pl.uservices.aggregatr.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Order;
import pl.uservices.aggregatr.aggregation.model.Version;

@RestController
@RequestMapping(value = "/ingredients", consumes = Version.AGGREGATOR_V1, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class IngredientsController {

    private final IngredientsAggregator ingredientsAggregator;

    @Autowired
    public IngredientsController(IngredientsAggregator ingredientsAggregator) {
        this.ingredientsAggregator = ingredientsAggregator;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Ingredients distributeIngredients(@RequestBody Order order, @RequestHeader("PROCESS-ID") String processId) {
        log.info("Starting process for process id [{}]", processId);
        return ingredientsAggregator.fetchIngredients(order, processId);
    }

}
