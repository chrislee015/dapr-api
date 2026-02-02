package com.example.api.application.pipeline;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.Command;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class MetricsBehavior implements PipelineBehavior<CqrsMessage> {

    private final MeterRegistry registry;

    public MetricsBehavior(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object apply(CqrsMessage message, Supplier<Object> next) {
        Instant start = Instant.now();
        String name = message.getClass().getSimpleName();
        String type = (message instanceof Command) ? "command" : "query";

        try {
            Object result = next.get();
            registry.timer("cqrs.execution", "type", type, "name", name, "status", "success")
                    .record(Duration.between(start, Instant.now()));
            return result;
        } catch (RuntimeException ex) {
            registry.timer("cqrs.execution", "type", type, "name", name, "status", "error")
                    .record(Duration.between(start, Instant.now()));
            throw ex;
        }
    }
}
