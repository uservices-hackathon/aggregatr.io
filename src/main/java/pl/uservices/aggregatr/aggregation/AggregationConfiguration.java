package pl.uservices.aggregatr.aggregation;

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;
import com.ofg.infrastructure.discovery.ServiceResolver;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.instrument.web.TraceFilter;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.regex.Pattern;

@Configuration
class AggregationConfiguration {

    @Value("${spring.sleuth.instrument.web.skipPattern:}") String skipPattern;

    @Autowired Trace trace;

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
    public Sampler<?> defaultSampler() {
        return new AlwaysSampler();
    }

    @Bean
    public FilterRegistrationBean traceWebFilter(ApplicationEventPublisher publisher) {
        Pattern pattern = org.springframework.util.StringUtils.hasText(this.skipPattern) ? Pattern.compile(this.skipPattern)
                : TraceFilter.DEFAULT_SKIP_PATTERN;
        TraceFilter filter = new TraceFilter(this.trace, pattern);
        filter.setApplicationEventPublisher(publisher);
        return new FilterRegistrationBean(filter);
    }

    @Bean
    @Primary
    public ServiceRestClient serviceRestClientWithRestTemplate(RestTemplate restTemplate, ServiceResolver serviceResolver, ServiceConfigurationResolver configurationResolver) {
        return new ServiceRestClient(restTemplate, serviceResolver, configurationResolver);
    }

    @Autowired RestTemplate restTemplate;
    @Autowired @Qualifier("requestFactory")  ClientHttpRequestFactory clientHttpRequestFactory;

    @PostConstruct
    void postConstruct() {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }

}

