package com.example.common.event.upcast;

import com.example.common.event.DomainEvent;

public interface EventUpcaster {
    DomainEvent upcast(DomainEvent event);
}
