package com.app.springsandboxone.services;

import com.app.springsandboxone.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;

@Service
public class FanOutService {

    private static final Logger logger = LoggerFactory.getLogger(FanOutService.class);
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final int CONTAINER_COUNT = 25;
    private static final String BASE_URL_TEMPLATE = "http://localhost:%d";
    private static final int BASE_PORT = 3000;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public FanOutService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // ... existing code ...

    /**
     * Execute HTTP request to a specific container using WebClient
     */
    private <T> TaskResult<T> executeHttpRequest(
            String containerName, 
            int port, 
            String endpoint, 
            String method, 
            Object requestBody, 
            TypeReference<T> responseType) {
    
        long startTime = System.currentTimeMillis();
        String baseUrl = String.format(BASE_URL_TEMPLATE, port);
    
        try {
            WebClient.RequestBodySpec requestSpec = webClient
                    .method(org.springframework.http.HttpMethod.valueOf(method.toUpperCase()))
                    .uri(baseUrl + endpoint)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");
        
            // Add request body if present
            Mono<String> responseMono;
            if (requestBody != null && ("POST".equals(method.toUpperCase()) || "PUT".equals(method.toUpperCase()))) {
                responseMono = requestSpec
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10));
            } else {
                responseMono = requestSpec
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10));
            }
        
            // Execute the request synchronously
            String responseBody = responseMono.block();
            long executionTime = System.currentTimeMillis() - startTime;
        
            // Parse response
            T data = null;
            if (responseBody != null && !responseBody.isEmpty() && !"DELETE".equals(method.toUpperCase())) {
                try {
                    data = objectMapper.readValue(responseBody, responseType);
                } catch (Exception parseException) {
                    logger.debug("Could not parse response body for {}: {}", containerName, parseException.getMessage());
                }
            }
        
            return TaskResult.success(containerName, endpoint, method, 200, data, executionTime);
        
        } catch (WebClientResponseException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return TaskResult.error(containerName, endpoint, method, e.getStatusCode().value(), 
                "HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return TaskResult.error(containerName, endpoint, method, -1, 
                "Request failed: " + e.getMessage(), executionTime);
        }
    }

    /**
     * Health check - ping all containers using WebClient
     */
    public List<TaskResult<String>> healthCheck() {
        logger.info("Performing health check on all {} containers - Main Thread: {}",
                CONTAINER_COUNT, Thread.currentThread().getName());

        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAll())) {
            List<StructuredTaskScope.Subtask<TaskResult<String>>> subtasks = new ArrayList<>();
            Instant start = Instant.now();
            for (int i = 1; i <= 1000; i++) {
                
                final String containerName = "j" + i;
                final int port = BASE_PORT + i;
                StructuredTaskScope.Subtask<TaskResult<String>> subtask = scope.fork(() -> {
                    String currentThreadName = Thread.currentThread().getName();
                    logger.info("Starting health check for {} on thread: {} is Virtual {}",
                            containerName, currentThreadName, Thread.currentThread().isVirtual());

                    long startTime = System.currentTimeMillis();
                    String baseUrl = String.format(BASE_URL_TEMPLATE, port);

                    try {
                        String response = webClient
                                .get()
                                .uri(baseUrl + "/posts/1")
                                .retrieve()
                                .bodyToMono(String.class)
                                .timeout(Duration.ofSeconds(5))
                                .block();

                        long executionTime = System.currentTimeMillis() - startTime;
                        logger.info("Using Thread {} Completed health check for {} on thread: {} in {}ms",
                                Thread.currentThread().isVirtual(), containerName, currentThreadName, executionTime);
                        return TaskResult.success(containerName, "/posts/1", "GET",
                                200, "OK", executionTime);

                    } catch (WebClientResponseException e) {
                        long executionTime = System.currentTimeMillis() - startTime;
                        logger.info("Using Thread {} Health check failed for {} on thread: {} in {}ms",
                                Thread.currentThread().isVirtual(), containerName, currentThreadName, executionTime);
                        return TaskResult.error(containerName, "/posts/1", "GET",
                                e.getStatusCode().value(), "Health check failed", executionTime);
                    } catch (Exception e) {
                        long executionTime = System.currentTimeMillis() - startTime;
                        logger.info("Using Thread {} Health check error for {} on thread: {} in {}ms",
                                Thread.currentThread().isVirtual(), containerName, currentThreadName, executionTime);
                        return TaskResult.error(containerName, "/posts/1", "GET",
                                -1, "Health check error: " + e.getMessage(), executionTime);
                    }
                });
                
                subtasks.add(subtask);
            }
            
            scope.join();
            logger.info(ANSI_YELLOW + "Completed health checks for {} containers in {}ms", subtasks.size(), Duration.between(start, Instant.now()).toMillis());
            List<TaskResult<String>> results = new ArrayList<>();
            for (var subtask : subtasks) {
                TaskResult<String> result = subtask.get();
                results.add(result);

                if (result.successful()) {
                    logger.info("Using Thread {} HEALTHY - Container: {}, Time: {}ms", Thread.currentThread().getName(),
                            result.containerName(), result.executionTimeMs());
                } else {
                    logger.warn("Using Thread {} UNHEALTHY - Container: {}, Error: {}, Time: {}ms",
                            Thread.currentThread().getName(), result.containerName(), result.errorMessage(), result.executionTimeMs());
                }
            }

            long healthyCount = results.stream().mapToLong(r -> r.successful() ? 1 : 0).sum();
            logger.info("Health check completed on thread: {}. Healthy: {}/{}",
                    Thread.currentThread().getName(), healthyCount, CONTAINER_COUNT);

            return results;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Health check interrupted on thread: {}", Thread.currentThread().getName(), e);
            return List.of();
        } catch (Exception e) {
            logger.error("Health check failed on thread: {}", Thread.currentThread().getName(), e);
            return List.of();
        }
    }
}
