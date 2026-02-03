package com.example.common.cqrs.bus;

import com.example.common.cqrs.Request;

import java.util.concurrent.CompletionStage;

public interface AsyncCqrsBus {
    <R> CompletionStage<R> dispatchAsync(Request message);
}
