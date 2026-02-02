package com.example.common.cqrs.handler;

import com.example.common.cqrs.Command;

public interface CommandHandler<C extends Command<R>, R> {
    R handle(C command);
}
