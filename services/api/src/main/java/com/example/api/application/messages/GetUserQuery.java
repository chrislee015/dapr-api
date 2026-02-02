package com.example.api.application.messages;

import com.example.common.cqrs.Query;
import com.example.common.cqrs.markers.TenantAware;

import java.util.UUID;

public record GetUserQuery(String tenantId, UUID id) implements Query<UserResponse>, TenantAware {
}
