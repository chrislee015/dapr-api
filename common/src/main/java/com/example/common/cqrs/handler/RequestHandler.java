package com.example.common.cqrs.handler;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.Response;
import com.example.common.cqrs.message.Http;

public sealed interface RequestHandler<X extends Request, Y extends  Response> permits CommandHandler, QueryHandler {
    Response handle(Request request);
    Http getRequestType();

    Class<X> getCqrsMessage();
    Class<Y> getResponseMessage();
}
