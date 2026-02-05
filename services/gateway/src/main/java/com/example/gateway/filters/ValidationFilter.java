package com.example.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ValidationFilter extends AbstractGatewayFilterFactory<ValidationFilter.Config>
        implements Ordered {

    public ValidationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            var headers = request.getHeaders();

            // Validate required headers
            if (isNullOrEmpty(headers.getFirst("User-Agent"))) {
                log.warn("Request missing User-Agent header");
                return Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "User-Agent header is required"));
            }

            // Validate authentication headers for protected routes
            String path = request.getPath().value();
            if (path.startsWith("/api/") || path.startsWith("/dapr/")) {
                if (isNullOrEmpty(headers.getFirst("Authorization"))) {
                    log.warn("Request missing Authorization header for protected route: {}", path);
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, "Authorization header is required"));
                }
            }

            return chain.filter(exchange);
        };
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    public static class Config {
        // Configuration properties if needed
    }
}