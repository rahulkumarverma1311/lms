package com.micro.cards.config;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.restclient.observation.ObservationRestClientCustomizer;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationConvention;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;

import java.util.concurrent.Executors;

@Configuration
public class HttpClientObservabilityConfig {

    @Bean
    RestClientCustomizer tracingRestClientCustomizer(ObservationRegistry observationRegistry){
        ClientRequestObservationConvention clientRequestObservationConvention= new DefaultClientRequestObservationConvention();
        return new ObservationRestClientCustomizer(observationRegistry,clientRequestObservationConvention);
    }


    @Bean
    Customizer<Resilience4JCircuitBreakerFactory> contextPropagationCustomizer(){
        ContextSnapshotFactory  snapshotFactory = ContextSnapshotFactory.builder().build();
        return factory->factory.configureExecutorService(ContextExecutorService.wrap(Executors.newCachedThreadPool(),snapshotFactory));
    }
}
