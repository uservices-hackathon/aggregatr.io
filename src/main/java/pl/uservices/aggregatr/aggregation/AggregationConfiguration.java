package pl.uservices.aggregatr.aggregation;

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;
import com.ofg.infrastructure.discovery.ServiceResolver;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
class AggregationConfiguration {
    @Bean
    IngredientsProperties ingredientsProperties(@Value("${ingredients.rootUrl:}") String rootUrl) {
        IngredientsProperties ingredientsProperties = new IngredientsProperties();
        ingredientsProperties.setRootUrl(StringUtils.defaultIfBlank(rootUrl,
                "http://localhost:" + String.valueOf(SocketUtils.findAvailableTcpPort())));
        return ingredientsProperties;
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    ExternalServicesStub externalServicesStub(IngredientsProperties  ingredientsProperties) throws IOException {
        return new ExternalServicesStub(ingredientsProperties);
    }

    @Bean
    @Primary
    public ServiceRestClient serviceRestClientWithRestTemplate(RestTemplate restTemplate, ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver, Trace trace) {
        return new ServiceRestClient(restTemplate, serviceResolver, configurationResolver, trace);
    }

    @Autowired RestTemplate restTemplate;
    @Autowired @Qualifier("requestFactory")  ClientHttpRequestFactory clientHttpRequestFactory;

    @PostConstruct
    void postConstruct() {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }

}

