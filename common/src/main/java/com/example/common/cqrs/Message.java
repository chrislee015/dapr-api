package com.example.common.cqrs;

import com.example.common.cqrs.message.Http;
import com.example.common.cqrs.message.IMessage;

import java.time.Instant;
import java.util.UUID;

public abstract class Message implements IMessage {

    private final UUID messageId;
    private final Http http;
    private final Instant createdAt;
    private final String message;
    private final Http type;

    protected Message(Http http, Instant createdAt, String message, Http type) {
        this.http = http;
        this.createdAt = createdAt;
        this.messageId = UUID.randomUUID();
        this.message = message;
        this.type = type;
    }

    @Override
    public String toString() { return message; }

    @Override
    public UUID getId() { return messageId; }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public Http getMessageType() {
        return http;
    }

    public abstract boolean supports(Class<?> clazz);
}
