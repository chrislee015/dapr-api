package com.example.common.cqrs;

public non-sealed interface Query<Q extends Message> extends Request {
    Message getQueryMessage();
}
