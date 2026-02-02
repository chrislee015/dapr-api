package com.example.common.event.store;

import com.example.common.event.DomainEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface EventStore {
    void append(String tenantId, UUID aggregateId, List<DomainEvent> events);
    List<DomainEvent> load(String tenantId, UUID aggregateId);

    default List<DomainEvent> loadFromVersion(String tenantId, UUID aggregateId, int fromVersion) {
        return load(tenantId, aggregateId);
    }

    default Stream<DomainEvent> streamAll(String tenantId) { return Stream.empty(); }
    default Stream<DomainEvent> streamFrom(String tenantId, Instant fromInclusive) { return Stream.empty(); }
}
