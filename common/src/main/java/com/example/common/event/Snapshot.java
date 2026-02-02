package com.example.common.event;

import java.time.Instant;
import java.util.UUID;

public record Snapshot<T>(
        UUID aggregateId,
        T state,
        int version,
        Instant createdAt
) {
}
