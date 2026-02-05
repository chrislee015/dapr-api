package com.example.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class HttpsRedirectFilter extends AbstractGatewayFilterFactory<HttpsRedirectFilter.Config>
    implements Ordered {

    public HttpsRedirectFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            var headers = request.getHeaders();
            
            // Check if request is HTTP and redirect to HTTPS
            String forwardedProto = headers.getFirst("X-Forwarded-Proto");
            String scheme = request.getURI().getScheme();
            
            if ("http".equals(scheme) || "http".equals(forwardedProto)) {
                log.info("Redirecting HTTP request to HTTPS: {}", request.getURI());
                
                var httpsUri = URI.create("https://localhost:8080" + request.getPath());
                var response = exchange.getResponse();
                response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
                response.getHeaders().setLocation(httpsUri);
                
                return response.setComplete();
            }
            
            return chain.filter(exchange);
        };
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    public static class Config {
        // Configuration properties if needed
    }
}
