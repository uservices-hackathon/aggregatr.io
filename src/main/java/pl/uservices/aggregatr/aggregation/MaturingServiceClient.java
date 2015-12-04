package pl.uservices.aggregatr.aggregation;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.uservices.aggregatr.aggregation.model.Ingredients;
import pl.uservices.aggregatr.aggregation.model.Version;

@FeignClient("dojrzewatr")
@RequestMapping(value = "/brew", consumes = Version.DOJRZEWATR_V1)
public interface MaturingServiceClient {
    @RequestMapping(method = RequestMethod.POST)
    void distributeIngredients(Ingredients ingredients);
}
