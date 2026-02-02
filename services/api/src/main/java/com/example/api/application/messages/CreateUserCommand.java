package com.example.api.application.messages;

import com.example.common.cqrs.Command;
import com.example.common.cqrs.markers.IdempotentCommand;
import com.example.common.cqrs.markers.TenantAware;

public record CreateUserCommand(
        String tenantId,
        String email,
        String idempotencyKey
) implements Command<UserResponse>, TenantAware, IdempotentCommand {
}
