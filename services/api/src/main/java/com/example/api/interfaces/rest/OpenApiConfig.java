package com.example.api.interfaces.rest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("API Service")
                .description("CQRS API service for the monorepo (springdoc-openapi).")
                .version("v1")
                .license(new License().name("Apache-2.0")));
    }
}
