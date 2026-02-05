package com.app.sandboxtwo.services;

import com.app.sandboxtwo.configuration.ExternalClientProperties;
import com.app.sandboxtwo.models.CreateClientRequest;
import com.app.sandboxtwo.models.CreateClientResponse;
import com.app.sandboxtwo.models.RuntimeFeignClient;
import feign.Feign;
import feign.ResponseInterceptor;
import feign.Retryer;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.CachingCapability;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.app.sandboxtwo.services.cache.CacheManagerService;

@Slf4j
@Service
public class DynamicFeignClientService {

    private final ExternalClientProperties clientProperties;
    private final Map<String, RuntimeFeignClient> clients = new ConcurrentHashMap<>();
    private final CacheManagerService cacheManagerService;

    @Autowired
    public DynamicFeignClientService(ExternalClientProperties clientProperties,
                                   CacheManagerService cacheManagerService) {
        this.clientProperties = clientProperties;
        this.cacheManagerService = cacheManagerService;
    }
    
    @PostConstruct
    public void initializeClients() {
        if (clientProperties.getClients() != null) {
            log.info("Initializing {} dynamic Feign clients", clientProperties.getClients().size());
            
            for (ExternalClientProperties.ClientConfig config : clientProperties.getClients()) {
                try {
                    createFeignClient(config.getId(), config.getName(), config.getUrl(), config.getApiKey());
                } catch (Exception e) {
                    log.error("Failed to create Feign client for {}: {}", config.getId(), e.getMessage(), e);
                }
            }
            
            log.info("Successfully initialized {}/{} dynamic Feign clients", 
                    clients.size(), clientProperties.getClients().size());
        }
    }
    
    public CreateClientResponse addClient(CreateClientRequest request) {
        String clientId = request.getId();
        
        // Check if client already exists
        if (clients.containsKey(clientId)) {
            return CreateClientResponse.error(clientId, "Client with ID '" + clientId + "' already exists");
        }
        
        // Validate URL format
        try {
            new URL(request.getUrl());
        } catch (MalformedURLException e) {
            return CreateClientResponse.error(clientId, "Invalid URL format: " + e.getMessage());
        }
        
        try {
            RuntimeFeignClient client = createFeignClient(
                    request.getId(),
                    request.getName(),
                    request.getUrl(),
                    request.getApiKey()
            );
            
            log.info("Dynamically created Feign client: {} -> {} ({})", 
                    clientId, request.getName(), request.getUrl());
            
            return CreateClientResponse.success(clientId, request.getName(), request.getUrl());
            
        } catch (Exception e) {
            log.error("Failed to create dynamic Feign client for {}: {}", clientId, e.getMessage(), e);
            return CreateClientResponse.error(clientId, "Failed to create client: " + e.getMessage());
        }
    }
    
    private RuntimeFeignClient createFeignClient(String id, String name, String url, String apiKey) {
        GenericFeignClient feignClient = Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .requestInterceptor(requestTemplate -> requestTemplate.header("X-API-KEY", apiKey))
                .requestInterceptor(requestTemplate -> requestTemplate.header("Accept", "application/json"))
                .retryer(Retryer.NEVER_RETRY)
                .logger(new Slf4jLogger(GenericFeignClient.class.getName() + "." + id))
                .target(GenericFeignClient.class, url);
        
        RuntimeFeignClient runtimeClient = new RuntimeFeignClient(id, name, url, apiKey, feignClient);
        clients.put(id, runtimeClient);
        
        return runtimeClient;
    }
    
    public boolean removeClient(String clientId) {
        RuntimeFeignClient removedClient = clients.remove(clientId);
        if (removedClient != null) {
            log.info("Removed Feign client: {} -> {}", clientId, removedClient.getName());
            return true;
        }
        return false;
    }
    
    public List<RuntimeFeignClient> getAllClients() {
        return List.copyOf(clients.values());
    }
    
    public int getClientCount() {
        return clients.size();
    }
    
    public Map<String, Object> executeGetPosts(String clientId) {
        return (Map<String, Object>) cacheManagerService.getCachedPosts(clientId, () -> {
            return getClient(clientId)
                    .map(client -> {
                        try {
                            List<Map<String, Object>> posts = client.getClient().getPosts(client.getApiKey());
                            return Map.<String, Object>of(
                                    "clientId", clientId,
                                    "clientName", client.getName(),
                                    "success", true,
                                    "data", posts,
                                    "count", posts.size()
                            );
                        } catch (Exception e) {
                            log.error("Error calling getPosts for client {}: {}", clientId, e.getMessage());
                            return Map.<String, Object>of(
                                    "clientId", clientId,
                                    "success", false,
                                    "error", e.getMessage()
                            );
                        }
                    })
                    .orElse(Map.of(
                            "clientId", clientId,
                            "success", false,
                            "error", "Client not found"
                    ));
        });
    }

    public Optional<RuntimeFeignClient> getClient(String clientId) {
        return Optional.<RuntimeFeignClient>of((RuntimeFeignClient) cacheManagerService.<RuntimeFeignClient>getCachedClient(clientId,
                () -> clients.<RuntimeFeignClient>computeIfAbsent(clientId, k -> {
                    return Optional.ofNullable(clients.<RuntimeFeignClient>get(clientId)).orElse(new RuntimeFeignClient(k, k, k, k, null));
                })));
    }
}
