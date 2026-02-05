package com.example.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config>
        implements Ordered {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            long startTime = System.currentTimeMillis();

            log.info("Incoming request: {} {} from {}",
                    request.getMethod(),
                    request.getURI(),
                    request.getRemoteAddress());

            return chain.filter(exchange)
                    .doFinally(signalType -> {
                        long endTime = System.currentTimeMillis();
                        var response = exchange.getResponse();

                        log.info("Outgoing response: {} {} - Status: {} - Duration: {}ms",
                                request.getMethod(),
                                request.getURI(),
                                response.getStatusCode(),
                                (endTime - startTime));
                    });
        };
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 3;
    }

    public static class Config {
        // Configuration properties if needed
    }
}