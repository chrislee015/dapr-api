package com.example.api.application.pipeline;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.function.Supplier;

public class CorrelationBehavior implements PipelineBehavior<Request> {
    @Override
    public Object apply(Request message, Supplier<Object> next) {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.isBlank()) correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        try { return next.get(); }
        finally { MDC.remove("correlationId"); }
    }
}
