package com.example.api.application.cqrs;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.bus.CqrsBus;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import com.example.common.cqrs.registry.HandlerRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class RegistryCqrsBus implements CqrsBus {

    private final HandlerRegistry registry;
    private final List<PipelineBehavior<Request>> behaviors;

    public RegistryCqrsBus(HandlerRegistry registry, List<PipelineBehavior<Request>> behaviors) {
        this.registry = registry;
        this.behaviors = behaviors;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R dispatch(Request message) {
        Supplier<Object> finalHandler = () -> registry.handle(message);

        Supplier<Object> chain = finalHandler;
        for (int i = behaviors.size() - 1; i >= 0; i--) {
            PipelineBehavior<Request> behavior = behaviors.get(i);
            Supplier<Object> next = chain;
            chain = () -> behavior.apply(message, next);
        }
        return (R) chain.get();
    }
}
