package com.example.api.application.cqrs;
import com.example.common.cqrs.handler.RequestHandler;
import com.example.common.cqrs.registry.HandlerRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ApiHandlerRegistry extends HandlerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ApiHandlerRegistry.class);

    public ApiHandlerRegistry(List<RequestHandler<?,?>> handlers) {
        super(handlers);
    }
    
    @PostConstruct
    public void init() {
        logger.info("Registered handlers:");
        super.handlerMap.forEach((key, value) ->
                value.forEach((clazz, handler) ->
                        logger.info(" {} -> {} -> {}", key.getSimpleName(), clazz.getSimpleName(), handler.getClass().getSimpleName())));
    }
}
