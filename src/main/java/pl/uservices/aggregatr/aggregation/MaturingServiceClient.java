package pl.uservices.aggregatr.aggregation;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Version;

@FeignClient("maturing")
@RequestMapping(value = "/brew", consumes = Version.MATURING_V1)
public interface MaturingServiceClient {
    @RequestMapping(method = RequestMethod.POST)
    void distributeIngredients(Ingredients ingredients, @RequestHeader("PROCESS-ID") String processId);
}