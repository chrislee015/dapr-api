package com.example.api.application.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdministrativeRestEndpoints {

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/version")
    public String version() {
        return "1.0.0";
    }

    @GetMapping("/stats")
    public String stats() {
        return "OK";
    }

    @GetMapping("/ping")
    public String ping() {
        return "OK";
    }

    @GetMapping("/test")
    public String status(Principal principal) {
        var p = principal;
        return ResponseEntity.ok().build().toString();
    }

}