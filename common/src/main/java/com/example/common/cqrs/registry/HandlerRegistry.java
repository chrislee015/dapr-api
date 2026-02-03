package com.example.common.cqrs.registry;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.handler.CommandHandler;
import com.example.common.cqrs.handler.QueryHandler;
import com.example.common.cqrs.handler.RequestHandler;
import com.example.common.cqrs.message.Http;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class HandlerRegistry {

    protected final Map<Http, Map<Class<?>, RequestHandler<?, ?>>> handlerMap;

    protected HandlerRegistry(List<RequestHandler<?, ?>> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.groupingBy(
                        RequestHandler::getRequestType,
                        Collectors.toMap(
                                RequestHandler::getCqrsMessage,
                                Function.identity()
                        )
                ));
    }

    public Object getHandler(Request request) {
        return handlerMap.get(request.getMessageType()).get(request.getClass());
    }

    public Object handle(Request request) {
        RequestHandler<?, ?> handler = (RequestHandler<?, ?>) getHandler(request);
        
        if (handler == null) {
            throw new IllegalStateException("No handler for " + request.getClass().getName());
        }

        return switch (handler) {
            case CommandHandler<?, ?> commandHandler -> commandHandler.handle(request);
            case QueryHandler<?, ?> queryHandler -> queryHandler.handle(request);
        };
    }
}
