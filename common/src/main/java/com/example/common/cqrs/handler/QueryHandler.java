package com.example.common.cqrs.handler;

import com.example.common.cqrs.Query;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
