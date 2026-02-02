package com.example.api.domain;

import com.example.common.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserAggregate {

    private UUID id;
    private String email;
    private int version;

    private final List<DomainEvent> uncommitted = new ArrayList<>();

    public static UserAggregate empty() {
        return new UserAggregate();
    }

    public UUID id() { return id; }
    public String email() { return email; }
    public int version() { return version; }
    public List<DomainEvent> uncommittedEvents() { return List.copyOf(uncommitted); }
    public void clearUncommitted() { uncommitted.clear(); }

    public void create(UUID id, String email, DomainEvent createdEvent) {
        // business rules would go here
        apply(createdEvent);
        uncommitted.add(createdEvent);
    }

    public void apply(DomainEvent event) {
        // In a real app, you’d pattern match on concrete events.
        // We keep this starter generic.
        this.id = event.aggregateId();
        this.version++;
    }
}
