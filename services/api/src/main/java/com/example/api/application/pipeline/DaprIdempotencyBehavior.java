package com.example.api.application.pipeline;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.markers.IdempotentCommand;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import io.dapr.client.DaprClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public class DaprIdempotencyBehavior implements PipelineBehavior<CqrsMessage> {

    private final DaprClient dapr;
    private static final Logger log = LoggerFactory.getLogger(DaprIdempotencyBehavior.class);
    
    public DaprIdempotencyBehavior(DaprClient dapr) {
        this.dapr = dapr;
    }

    private static final String IDEMPOTENCY_STATE_STORE = "statestore";

    @Override
    public Object apply(CqrsMessage message, Supplier<Object> next) {
        if (!(message instanceof IdempotentCommand cmd)) return next.get();

        if (isCommandAlreadyProcessed(cmd)) return null;

        Object result = next.get();
        markCommandAsProcessed(cmd);
        return result;
    }

    private boolean isCommandAlreadyProcessed(IdempotentCommand cmd) {
        String key = cmd.idempotencyKey();
        Boolean exists = Objects.requireNonNull(dapr.getState(IDEMPOTENCY_STATE_STORE, key, Boolean.class).block()).getValue();
        return Boolean.TRUE.equals(exists);
    }

    private void markCommandAsProcessed(IdempotentCommand cmd) {
        String key = cmd.idempotencyKey();
        try {
            dapr.saveState(IDEMPOTENCY_STATE_STORE, key, true).block();
        } catch (Exception e) {
            log.error("Failed to mark command as processed for key {}: {}", key, e.getMessage());
            throw new RuntimeException("Failed to persist idempotency marker", e);
        }
    }
}
