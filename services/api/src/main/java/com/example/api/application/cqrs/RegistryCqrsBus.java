package com.example.api.application.cqrs;

import com.example.common.cqrs.CqrsMessage;
import com.example.common.cqrs.bus.CqrsBus;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class RegistryCqrsBus implements CqrsBus {

    private final HandlerRegistry registry;
    private final List<PipelineBehavior<CqrsMessage>> behaviors;

    public RegistryCqrsBus(HandlerRegistry registry, List<PipelineBehavior<CqrsMessage>> behaviors) {
        this.registry = registry;
        this.behaviors = behaviors;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R dispatch(CqrsMessage message) {
        Supplier<Object> finalHandler = () -> registry.handle(message);

        Supplier<Object> chain = finalHandler;
        for (int i = behaviors.size() - 1; i >= 0; i--) {
            PipelineBehavior<CqrsMessage> behavior = behaviors.get(i);
            Supplier<Object> next = chain;
            chain = () -> behavior.apply(message, next);
        }
        return (R) chain.get();
    }
}
