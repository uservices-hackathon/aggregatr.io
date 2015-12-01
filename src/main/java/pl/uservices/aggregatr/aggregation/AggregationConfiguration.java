package pl.uservices.aggregatr.aggregation;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.SocketUtils;

@Configuration
@Import(ExecutorConfig.class)
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

//    @Bean
//    ScheduledExecutorService scheduledExecutorService() {
//        return Executors.newScheduledThreadPool(10);
//    }
//
//    @Bean
//    RetryExecutor notRetryingRetryExecutor() {
//        return new AsyncRetryExecutor(scheduledExecutorService()).dontRetry();
//    }

//    @Bean
//    @Primary
//    public ServiceRestClient serviceRestClientWithRestTemplate(RestTemplate restTemplate, ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver, Trace trace) {
//        return new ServiceRestClient(restTemplate, serviceResolver, configurationResolver, trace);
//    }
//    @Bean
//    @Primary
//    public ServiceRestClient serviceRestClientWithRestTemplate(RestTemplate restTemplate, ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver, Trace trace) {
//        return new ServiceRestClient(restTemplate, serviceResolver, configurationResolver, trace);
//    }
//
//    @Autowired RestTemplate restTemplate;
//    @Autowired @Qualifier("requestFactory")  ClientHttpRequestFactory clientHttpRequestFactory;
//
//    @PostConstruct
//    void postConstruct() {
//        restTemplate.setRequestFactory(clientHttpRequestFactory);
//    }

}

