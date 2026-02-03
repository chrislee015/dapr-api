package com.example.api.application.messages.response;

import com.example.common.cqrs.Message;
import com.example.common.cqrs.Response;
import com.example.common.cqrs.message.Http;

import java.time.Instant;
import java.util.UUID;

import java.util.UUID;

public class UserResponse extends Message implements Response {
    private final UUID id;
    private final String email;

    public UserResponse(UUID id, String email) {
        super(Http.GET, Instant.now(), "User response", Http.GET);
        this.id = id;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getMessage() {
        return "";
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserResponse.class.equals(clazz);
    }
}
