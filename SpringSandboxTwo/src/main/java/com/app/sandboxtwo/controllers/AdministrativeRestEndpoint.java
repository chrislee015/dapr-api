package com.app.sandboxtwo.controllers;

import com.app.sandboxtwo.models.CreateClientRequest;
import com.app.sandboxtwo.models.CreateClientResponse;
import com.app.sandboxtwo.models.RuntimeFeignClient;
import com.app.sandboxtwo.services.DynamicFeignClientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdministrativeRestEndpoint {
    
    private final DynamicFeignClientService dynamicFeignClientService;
    
    @Autowired
    public AdministrativeRestEndpoint(DynamicFeignClientService dynamicFeignClientService) {
        this.dynamicFeignClientService = dynamicFeignClientService;
    }
    
    /**
     * Create a new Feign client at runtime
     */
    @PostMapping("/clients")
    public ResponseEntity<CreateClientResponse> createClient(@Valid @RequestBody CreateClientRequest request) {
        log.info("Administrative request to create new client: {} -> {} ({})", 
                request.getId(), request.getName(), request.getUrl());
        
        CreateClientResponse response = dynamicFeignClientService.addClient(request);
        
        if (response.isSuccess()) {
            log.info("Successfully created client: {}", response.getClientId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            log.warn("Failed to create client {}: {}", request.getId(), response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Remove a Feign client at runtime
     */
    @DeleteMapping("/clients/{clientId}")
    public ResponseEntity<Map<String, Object>> removeClient(@PathVariable String clientId) {
        log.info("Administrative request to remove client: {}", clientId);
        
        boolean removed = dynamicFeignClientService.removeClient(clientId);
        
        if (removed) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Client removed successfully",
                    "clientId", clientId,
                    "timestamp", Instant.now()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Client not found",
                    "clientId", clientId,
                    "timestamp", Instant.now()
            ));
        }
    }
    
    /**
     * Get system statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        List<RuntimeFeignClient> clients = dynamicFeignClientService.getAllClients();
        
        Map<String, Object> stats = Map.of(
                "totalClients", clients.size(),
                "timestamp", Instant.now(),
                "clients", clients.stream()
                        .map(client -> Map.of(
                                "id", client.getId(),
                                "name", client.getName(),
                                "url", client.getUrl()
                        ))
                        .toList()
        );
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Batch create multiple clients
     */
    @PostMapping("/clients/batch")
    public ResponseEntity<Map<String, Object>> createMultipleClients(
            @Valid @RequestBody List<CreateClientRequest> requests) {
        
        log.info("Administrative request to create {} clients in batch", requests.size());
        
        Map<String, CreateClientResponse> results = new java.util.HashMap<>();
        int successCount = 0;
        
        for (CreateClientRequest request : requests) {
            CreateClientResponse response = dynamicFeignClientService.addClient(request);
            results.put(request.getId(), response);
            if (response.isSuccess()) {
                successCount++;
            }
        }
        
        Map<String, Object> batchResponse = Map.of(
                "total", requests.size(),
                "successful", successCount,
                "failed", requests.size() - successCount,
                "results", results,
                "timestamp", Instant.now()
        );
        
        HttpStatus status = successCount == requests.size() ? HttpStatus.CREATED : 
                           successCount > 0 ? HttpStatus.PARTIAL_CONTENT : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(batchResponse);
    }
    
    /**
     * Test a specific client immediately after creation
     */
    @PostMapping("/clients/{clientId}/test")
    public ResponseEntity<Map<String, Object>> testNewClient(@PathVariable String clientId) {
        log.info("Administrative test request for client: {}", clientId);
        
        Map<String, Object> testResult = dynamicFeignClientService.executeGetPosts(clientId);
        
        return ResponseEntity.ok(Map.of(
                "clientId", clientId,
                "testResult", testResult,
                "timestamp", Instant.now()
        ));
    }
    
    /**
     * Create and immediately test a new client
     */
    @PostMapping("/clients/create-and-test")
    public ResponseEntity<Map<String, Object>> createAndTestClient(@Valid @RequestBody CreateClientRequest request) {
        log.info("Administrative request to create and test client: {}", request.getId());
        
        // Create the client
        CreateClientResponse createResponse = dynamicFeignClientService.addClient(request);
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("createResult", createResponse);
        response.put("timestamp", Instant.now());
        
        if (createResponse.isSuccess()) {
            // Test the client immediately
            Map<String, Object> testResult = dynamicFeignClientService.executeGetPosts(request.getId());
            response.put("testResult", testResult);
            
            log.info("Client {} created and tested successfully", request.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            log.warn("Failed to create client {}: {}", request.getId(), createResponse.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
