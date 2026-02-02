package com.example.common.cqrs.pipeline;

import com.example.common.cqrs.CqrsMessage;

import java.util.function.Supplier;

public interface PipelineBehavior<T extends CqrsMessage> {
    Object apply(T message, Supplier<Object> next);
}
