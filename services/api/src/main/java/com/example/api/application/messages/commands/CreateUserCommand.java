package com.example.api.application.messages.commands;

import com.example.api.application.messages.response.UserResponse;
import com.example.common.cqrs.Command;
import com.example.common.cqrs.markers.IdempotentCommand;
import com.example.common.cqrs.markers.TenantAware;
import com.example.common.cqrs.message.Http;

import java.time.Instant;
import java.util.UUID;

public record CreateUserCommand(
        String tenantId,
        String email,
        String idempotencyKey
) implements Command<UserResponse>, TenantAware, IdempotentCommand {

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
}
