package com.example.common.cqrs.registry;

import com.example.common.cqrs.Request;

public interface HandlerDispatcher {

    Object dispatch(Request message);
}
