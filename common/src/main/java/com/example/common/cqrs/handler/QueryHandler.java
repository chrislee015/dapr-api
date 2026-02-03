package com.example.common.cqrs.handler;

import com.example.common.cqrs.Message;
import com.example.common.cqrs.Query;
import com.example.common.cqrs.Response;

public non-sealed interface QueryHandler<Q extends Query<? extends Message>, R extends Response> extends RequestHandler<Q, R> {
    Response handle(Query<? extends Message> message);
}
