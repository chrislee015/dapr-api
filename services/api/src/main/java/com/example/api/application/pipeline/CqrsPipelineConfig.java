package com.example.api.application.pipeline;

import com.example.api.application.messages.CreateUserCommand;
import com.example.common.cqrs.CqrsMessage;
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
    PipelineBehavior<CqrsMessage> correlation() { return new CorrelationBehavior(); }

    @Bean @Order(2)
    PipelineBehavior<CqrsMessage> tenant() { return new TenantContextBehavior(); }

    @Bean @Order(3)
    PipelineBehavior<CqrsMessage> security() { return new SecurityBehavior(); }

    @Bean @Order(4)
    PipelineBehavior<CqrsMessage> rateLimit() { return new RateLimitBehavior(Map.of(CreateUserCommand.class, 20)); }

    @Bean @Order(5)
    PipelineBehavior<CqrsMessage> validation(Validator validator) { return new ValidationBehavior(validator); }

    @Bean @Order(6)
    PipelineBehavior<CqrsMessage> idempotency(DaprClient dapr) { return new DaprIdempotencyBehavior(dapr); }

    @Bean @Order(7)
    @ConditionalOnBean(PlatformTransactionManager.class)
    PipelineBehavior<CqrsMessage> tx(PlatformTransactionManager txm) { return new TransactionBehavior(txm); }

    @Bean @Order(8)
    PipelineBehavior<CqrsMessage> timeout() { return new TimeoutBehavior(Duration.ofSeconds(2)); }

    @Bean @Order(9)
    PipelineBehavior<CqrsMessage> metrics(MeterRegistry registry) { return new MetricsBehavior(registry); }

    @Bean @Order(10)
    PipelineBehavior<CqrsMessage> logging() { return new LoggingBehavior(); }
}
