package com.example.api.application.handler;

import com.example.api.application.messages.CreateUserCommand;
import com.example.api.application.messages.UserResponse;
import com.example.common.cqrs.handler.CommandHandler;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateUserHandler implements CommandHandler<CreateUserCommand, UserResponse> {

    @Override
    public UserResponse handle(CreateUserCommand command) {
        // Starter stub: replace with full event-sourced workflow.
        return new UserResponse(UUID.randomUUID(), command.email());
    }
}
