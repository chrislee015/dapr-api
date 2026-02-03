package com.example.common.cqrs.pipeline;

import com.example.common.cqrs.Request;

import java.util.function.Supplier;

public interface PipelineBehavior<T extends Request> {
    Object apply(T message, Supplier<Object> next);
}
