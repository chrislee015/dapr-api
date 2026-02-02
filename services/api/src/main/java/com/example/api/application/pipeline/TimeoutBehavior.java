package com.example.api.application.pipeline;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.pipeline.PipelineBehavior;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class TimeoutBehavior implements PipelineBehavior<CqrsMessage> {

    private final Duration maxDuration;

    public TimeoutBehavior(Duration maxDuration) {
        this.maxDuration = maxDuration;
    }

    @Override
    public Object apply(CqrsMessage message, Supplier<Object> next) {
        Instant start = Instant.now();
        Object result = next.get();
        Duration elapsed = Duration.between(start, Instant.now());
        if (elapsed.compareTo(maxDuration) > 0) {
            throw new RuntimeException(new TimeoutException(message.getClass().getSimpleName() + " exceeded " + maxDuration));
        }
        return result;
    }
}
