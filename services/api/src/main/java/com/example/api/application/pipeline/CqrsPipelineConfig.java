package com.example.api.application.pipeline;

import com.example.api.application.messages.commands.CreateUserCommand;
import com.example.common.cqrs.Request;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import io.dapr.client.DaprClient;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Validator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CqrsPipelineConfig {

    @Bean @Order(1)
    PipelineBehavior<Request> correlation() { return new CorrelationBehavior(); }

    @Bean @Order(2)
    PipelineBehavior<Request> tenant() { return new TenantContextBehavior(); }

    @Bean(name = "controller_security") @Order(3)
    PipelineBehavior<Request> security() { return new SecurityBehavior(); }

    @Bean @Order(4)
    PipelineBehavior<Request> rateLimit() { return new RateLimitBehavior(Map.of(CreateUserCommand.class, 20)); }

    @Bean @Order(5)
    PipelineBehavior<Request> validation(Validator validator) { return new ValidationBehavior(validator); }

    @Bean @Order(6)
    PipelineBehavior<Request> idempotency(DaprClient dapr) { return new DaprIdempotencyBehavior(dapr); }

    @Bean @Order(7)
    @ConditionalOnBean(PlatformTransactionManager.class)
    PipelineBehavior<Request> tx(PlatformTransactionManager txm) { return new TransactionBehavior(txm); }

    @Bean @Order(8)
    PipelineBehavior<Request> timeout() { return new TimeoutBehavior(Duration.ofSeconds(2)); }

    @Bean @Order(9)
    PipelineBehavior<Request> metrics(MeterRegistry registry) { return new MetricsBehavior(registry); }

    @Bean @Order(10)
    PipelineBehavior<Request> logging() { return new LoggingBehavior(); }
}
