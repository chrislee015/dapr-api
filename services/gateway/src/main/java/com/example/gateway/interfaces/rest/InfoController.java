package com.example.gateway.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class InfoController {

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "service", "gateway",
                "time", Instant.now().toString()
        );
    }
}
