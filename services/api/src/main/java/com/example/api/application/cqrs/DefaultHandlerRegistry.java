package com.example.api.application.cqrs;

import com.example.api.application.handler.CreateUserHandler;
import com.example.api.application.handler.GetUserHandler;
import com.example.api.application.messages.CreateUserCommand;
import com.example.api.application.messages.GetUserQuery;
import com.example.common.cqrs.CqrsMessage;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class DefaultHandlerRegistry implements HandlerRegistry {

    private final Map<Class<?>, Function<CqrsMessage, Object>> handlers;

    public DefaultHandlerRegistry(CreateUserHandler createUser, GetUserHandler getUser) {
        this.handlers = Map.of(
                CreateUserCommand.class, msg -> createUser.handle((CreateUserCommand) msg),
                GetUserQuery.class, msg -> getUser.handle((GetUserQuery) msg)
        );
    }

    @Override
    public Object handle(CqrsMessage message) {
        Function<CqrsMessage, Object> fn = handlers.get(message.getClass());
        if (fn == null) throw new IllegalStateException("No handler for " + message.getClass().getName());
        return fn.apply(message);
    }
}
