package com.example.common.cqrs.markers;

public interface IdempotentCommand {
    String idempotencyKey();
}
