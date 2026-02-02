package com.example.api.application.messages;

import com.example.common.cqrs.Response;

import java.util.UUID;

public record UserResponse(UUID id, String email) implements Response {
}
