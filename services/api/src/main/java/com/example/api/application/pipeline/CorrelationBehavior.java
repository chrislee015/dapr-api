package com.example.api.application.pipeline;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.function.Supplier;

public class CorrelationBehavior implements PipelineBehavior<CqrsMessage> {
    @Override
    public Object apply(CqrsMessage message, Supplier<Object> next) {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isBlank()) correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        try { return next.get(); }
        finally { MDC.remove("correlationId"); }
    }
}
