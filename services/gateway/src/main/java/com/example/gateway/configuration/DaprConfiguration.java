package com.example.gateway.configuration;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "gateway.routing.mode", havingValue = "dapr")
public class DaprConfiguration {

    @Bean
    public DaprClient daprClient() {
        log.info("Initializing DAPR client for sidecar communication");
        return new DaprClientBuilder().build();
    }
}