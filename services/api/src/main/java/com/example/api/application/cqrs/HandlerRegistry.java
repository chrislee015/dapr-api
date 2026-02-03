package com.example.api.application.cqrs;

import com.example.common.cqrs.Request;

public interface HandlerRegistry {
    Object handle(Request message);
}
