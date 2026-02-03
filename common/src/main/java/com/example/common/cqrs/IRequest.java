package com.example.common.cqrs;

import com.example.common.cqrs.message.Http;

import java.time.Instant;
import java.util.UUID;

public interface IRequest {
    UUID getId();
    Instant getCreatedAt();
    Http getMessageType();
}
