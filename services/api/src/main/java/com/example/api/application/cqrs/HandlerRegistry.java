package com.example.api.application.cqrs;

import com.example.common.cqrs.CqrsMessage;

public interface HandlerRegistry {
    Object handle(CqrsMessage message);
}
