package com.example.api.application.pipeline;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class LoggingBehavior implements PipelineBehavior<CqrsMessage> {

    private static final Logger log = LoggerFactory.getLogger(LoggingBehavior.class);

    @Override
    public Object apply(CqrsMessage message, Supplier<Object> next) {
        Instant start = Instant.now();
        try {
            Object result = next.get();
            log.info("CQRS handled {} in {} ms", message.getClass().getSimpleName(),
                    Duration.between(start, Instant.now()).toMillis());
            return result;
        } catch (RuntimeException ex) {
            log.error("CQRS failed {} after {} ms", message.getClass().getSimpleName(),
                    Duration.between(start, Instant.now()).toMillis(), ex);
            throw ex;
        }
    }
}
