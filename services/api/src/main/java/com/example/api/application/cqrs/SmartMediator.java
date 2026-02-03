//package com.example.api.application.cqrs;
//
//import com.example.common.cqrs.Request;
//import com.example.common.cqrs.handler.RequestHandler;
//import com.example.common.cqrs.registry.HandlerRegistry;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//public class ApiDispatcher {
//
//    private final Map<Class<? extends Request>, RequestHandler> registry;
//    private final List<CommandInterceptor> interceptors;
//
//    // Spring injects the Registry (from previous step) AND the list of Interceptors
//    public SmartMediator(HandlerRegistry registry, List<CommandInterceptor> interceptors) {
//        this.registry = registry.getMap(); // Assume registry exposes the map
//        this.interceptors = interceptors;
//    }
//
//    public ApiDispatcher(Map<Class<? extends Request>, RequestHandler> registry) {
//        this.registry = registry;
//    }
//
//    public <R> R dispatch(Request<R> request) {
//
//// PHASE 1: THE PIPELINE
//// Loop through all interceptors (Logging -> Validation -> Security -> etc.)
//        for (CommandInterceptor interceptor : interceptors) {
//            interceptor.preHandle(request);
//        }
//
//// PHASE 2: THE ROUTING
//// Only if all interceptors pass do we look up the handler
//        RequestHandler<Request<R>, R> handler = registry.get(request.getClass());
//
//        if (handler == null) {
//            throw new IllegalArgumentException("No handler for " + request.getClass());
//        }
//
//// PHASE 3: EXECUTION
//        return handler.handle(request);
//    }
//}