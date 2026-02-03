package com.example.api.application.messages.queries;

import com.example.api.application.messages.response.UserResponse;
import com.example.common.cqrs.Message;
import com.example.common.cqrs.Query;
import com.example.common.cqrs.markers.TenantAware;
import com.example.common.cqrs.message.Http;

import java.time.Instant;
import java.util.UUID;

public record GetUserQuery(String tenantId, UUID id) implements Query<UserResponse>, TenantAware {


    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public Instant getCreatedAt() {
        return null;
    }

    @Override
    public Http getMessageType() {
        return null;
    }

    @Override
    public Message getQueryMessage() {
        return null;
    }
}
