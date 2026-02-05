package com.app.sandboxtwo.controllers;

import com.app.sandboxtwo.models.RuntimeFeignClient;
import com.app.sandboxtwo.services.DynamicFeignClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dynamic-clients")
public class DynamicClientController {
    
    private final DynamicFeignClientService dynamicFeignClientService;
    
    @Autowired
    public DynamicClientController(DynamicFeignClientService dynamicFeignClientService) {
        this.dynamicFeignClientService = dynamicFeignClientService;
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<RuntimeFeignClient>> listClients() {
        List<RuntimeFeignClient> clients = dynamicFeignClientService.getAllClients();
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/{clientId}/posts")
    public ResponseEntity<Map<String, Object>> getPosts(@PathVariable String clientId) {
        Map<String, Object> result = dynamicFeignClientService.executeGetPosts(clientId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{clientId}/info")
    public ResponseEntity<RuntimeFeignClient> getClientInfo(@PathVariable String clientId) {
        return dynamicFeignClientService.getClient(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/test-all")
    public ResponseEntity<Map<String, Object>> testAllClients() {
        List<RuntimeFeignClient> clients = dynamicFeignClientService.getAllClients();
        Map<String, Object> results = new java.util.HashMap<>();
        
        for (RuntimeFeignClient client : clients) {
            Map<String, Object> result = dynamicFeignClientService.executeGetPosts(client.getId());
            results.put(client.getId(), result);
        }
        
        return ResponseEntity.ok(Map.of(
                "timestamp", java.time.Instant.now(),
                "totalClients", clients.size(),
                "results", results
        ));
    }
}
