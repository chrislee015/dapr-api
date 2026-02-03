package com.example.common.cqrs.message;

import java.time.Instant;
import java.util.UUID;

public interface IMessage {
    UUID getId();
    Instant getCreatedAt();
    Http getMessageType();
    String getMessage();
}
