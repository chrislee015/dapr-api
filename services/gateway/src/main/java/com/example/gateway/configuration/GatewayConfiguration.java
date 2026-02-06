//package com.example.gateway.configuration;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//public class GatewayConfiguration {
//
//    @Value("${gateway.routing.mode:api}")
//    private String routingMode;
//
//    @Value("${gateway.routing.api.uri:http://localhost:8081}")
//    private String apiUri;
//
//    @Value("${gateway.routing.dapr.uri:http://localhost:3500}")
//    private String daprUri;
//
//    @Value("${gateway.routing.dapr.app-id:api-service}")
//    private String daprAppId;
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        log.info("Configuring routes with routing mode: {}", routingMode);
//
//        return builder.routes()
//                .route("api-route", r -> r
//                        .path("/api/**")
//                        .and()
//                        .header("X-Forwarded-Proto", "https")
//                        .filters(f -> f
//                                .stripPrefix(1)
//                                .filter(new com.example.gateway.filter.AuthenticationFilter().apply(
//                                        new com.example.gateway.filter.AuthenticationFilter.Config()))
//                                .addRequestHeader("X-Gateway-Route", "api")
//                        )
//                        .uri("api".equals(routingMode) ? apiUri : daprUri + "/v1.0/invoke/" + daprAppId + "/method")
//                )
//                .route("health-route", r -> r
//                        .path("/actuator/health")
//                        .uri("forward:/actuator/health")
//                )
//                .build();
//    }
//}