package com.example.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalErrorHandlingFilter extends AbstractGatewayFilterFactory<GlobalErrorHandlingFilter.Config>
        implements Ordered {

    public GlobalErrorHandlingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                    .onErrorResume(error -> {
                        log.error("Error processing request: {}", error.getMessage(), error);

                        var response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                        response.getHeaders().add("Content-Type", MediaType.TEXT_HTML_VALUE);

                        String errorPage = generateErrorPage(error);
                        var buffer = response.bufferFactory().wrap(errorPage.getBytes());

                        return response.writeWith(Mono.just(buffer));
                    });
        };
    }

    private String generateErrorPage(Throwable error) {
        String errorMessage = error instanceof ResponseStatusException
                ? ((ResponseStatusException) error).getReason()
                : "An unexpected error occurred";

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Gateway Error</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 50px; }
                    .error-container { 
                        max-width: 600px; 
                        margin: 0 auto; 
                        padding: 20px; 
                        border: 1px solid #ddd; 
                        border-radius: 5px; 
                    }
                    .error-title { color: #d9534f; }
                </style>
            </head>
            <body>
                <div class="error-container">
                    <h1 class="error-title">Gateway Error</h1>
                    <p>We're sorry, but something went wrong while processing your request.</p>
                    <p><strong>Error:</strong> %s</p>
                    <p>Please try again later or contact support if the problem persists.</p>
                    <a href="https://localhost:8080/">Return to Home</a>
                </div>
            </body>
            </html>
            """.formatted(errorMessage);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    public static class Config {
        // Configuration properties if needed
    }
}