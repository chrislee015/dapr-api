package com.example.common.cqrs.handler;

import com.example.common.cqrs.Command;
import com.example.common.cqrs.Message;
import com.example.common.cqrs.Response;

public non-sealed interface CommandHandler<C extends Command<? extends Message>, R extends Response> extends RequestHandler<C, R> {
    Response handle(Command<? extends Message> message);
}
