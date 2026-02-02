package com.example.api.infrastructure.events;

import com.example.common.event.DomainEvent;
import com.example.common.event.store.EventStore;
import io.dapr.client.DaprClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DaprEventStore implements EventStore {

    private final DaprClient dapr;

    public DaprEventStore(DaprClient dapr) {
        this.dapr = dapr;
    }

    private static String key(String tenantId, UUID aggregateId) {
        return tenantId + ":" + aggregateId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void append(String tenantId, UUID aggregateId, List<DomainEvent> events) {
        String k = key(tenantId, aggregateId);

        List<DomainEvent> existing = Objects.requireNonNull(dapr.getState("statestore", k, List.class).block()).getValue();
        if (existing == null) existing = new ArrayList<>();

        existing = new ArrayList<>(existing);
        existing.addAll(events);

        dapr.saveState("statestore", k, existing).block();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DomainEvent> load(String tenantId, UUID aggregateId) {
        String k = key(tenantId, aggregateId);
        List<DomainEvent> existing = Objects.requireNonNull(dapr.getState("statestore", k, List.class).block()).getValue();
        return existing == null ? List.of() : existing;
    }
}
