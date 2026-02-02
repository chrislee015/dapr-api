package com.example.api.application.pipeline;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.pipeline.PipelineBehavior;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class RateLimitBehavior implements PipelineBehavior<CqrsMessage> {

    private final Map<Class<?>, Semaphore> limits = new ConcurrentHashMap<>();

    public RateLimitBehavior(Map<Class<?>, Integer> maxConcurrentByType) {
        maxConcurrentByType.forEach((cls, max) -> limits.put(cls, new Semaphore(max)));
    }

    @Override
    public Object apply(CqrsMessage message, Supplier<Object> next) {
        Semaphore sem = limits.get(message.getClass());
        if (sem == null) return next.get();

        if (!sem.tryAcquire()) throw new java.util.concurrent.RejectedExecutionException("Rate limit exceeded");
        try { return next.get(); }
        finally { sem.release(); }
    }
}
