package com.example.gateway.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/actuator")
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${gateway.routing.mode:api}")
    private String routingMode;

    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        log.debug("Health check requested");
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "application", applicationName,
            "routingMode", routingMode,
            "timestamp", LocalDateTime.now(),
            "details", Map.of(
                "gateway", "running",
                "security", "enabled",
                "https", "enforced"
            )
        );
        
        return Mono.just(ResponseEntity.ok(health));
    }
}
