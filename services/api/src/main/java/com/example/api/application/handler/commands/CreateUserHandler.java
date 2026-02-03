package com.example.api.application.handler.commands;

import com.example.api.application.messages.commands.CreateUserCommand;
import com.example.api.application.messages.response.UserResponse;
import com.example.common.cqrs.Command;
import com.example.common.cqrs.Message;
import com.example.common.cqrs.Request;
import com.example.common.cqrs.Response;
import com.example.common.cqrs.handler.CommandHandler;
import com.example.common.cqrs.message.Http;
import org.springframework.stereotype.Service;

@Service
public class CreateUserHandler implements CommandHandler<CreateUserCommand, UserResponse> {

    @Override
    public UserResponse handle(Request command) {
        return null;
    }

    @Override
    public Http getRequestType() {
        return Http.POST;
    }

    @Override
    public Class<CreateUserCommand> getCqrsMessage() {
        return CreateUserCommand.class;
    }

    @Override
    public Class<UserResponse> getResponseMessage() {
        return UserResponse.class;
    }

    @Override
    public Response handle(Command<? extends Message> message) {
        return null;
    }
}
