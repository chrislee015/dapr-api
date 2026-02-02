package com.example.common.event.store;

import com.example.common.event.Snapshot;

import java.util.Optional;
import java.util.UUID;

public interface SnapshotStore<T> {
    Optional<Snapshot<T>> load(String tenantId, UUID aggregateId);
    void save(String tenantId, Snapshot<T> snapshot);
}
