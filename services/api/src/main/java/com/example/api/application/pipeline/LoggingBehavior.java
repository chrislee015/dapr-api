package com.example.api.application.pipeline;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class LoggingBehavior implements PipelineBehavior<Request> {

    private static final Logger log = LoggerFactory.getLogger(LoggingBehavior.class);

    @Override
    public Object apply(Request message, Supplier<Object> next) {
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
