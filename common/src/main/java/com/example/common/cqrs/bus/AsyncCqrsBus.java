package com.example.common.cqrs.bus;

import com.example.common.cqrs.CqrsMessage;

import java.util.concurrent.CompletionStage;

public interface AsyncCqrsBus {
    <R> CompletionStage<R> dispatchAsync(CqrsMessage message);
}
