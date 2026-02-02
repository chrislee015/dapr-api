package com.example.api.infrastructure.snapshots;

import com.example.common.event.Snapshot;
import com.example.common.event.store.SnapshotStore;
import io.dapr.client.DaprClient;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class DaprSnapshotStore<T> implements SnapshotStore<T> {

    private final DaprClient dapr;

    public DaprSnapshotStore(DaprClient dapr) {
        this.dapr = dapr;
    }

    private static String key(String tenantId, UUID aggregateId) {
        return tenantId + ":snapshot:" + aggregateId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Snapshot<T>> load(String tenantId, UUID aggregateId) {
        Snapshot<T> snap = Objects.requireNonNull(dapr.getState("statestore", key(tenantId, aggregateId), Snapshot.class).block()).getValue();
        return Optional.ofNullable(snap);
    }

    @Override
    public void save(String tenantId, Snapshot<T> snapshot) {
        dapr.saveState("statestore", key(tenantId, snapshot.aggregateId()), snapshot).block();
    }
}
