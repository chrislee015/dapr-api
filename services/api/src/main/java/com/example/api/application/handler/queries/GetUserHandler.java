package com.example.api.application.handler.queries;

import com.example.api.application.messages.queries.GetUserQuery;
import com.example.api.application.messages.response.UserResponse;
import com.example.common.cqrs.Message;
import com.example.common.cqrs.Query;
import com.example.common.cqrs.Request;
import com.example.common.cqrs.Response;
import com.example.common.cqrs.handler.QueryHandler;
import com.example.common.cqrs.message.Http;
import org.springframework.stereotype.Service;

@Service
public class GetUserHandler implements QueryHandler<GetUserQuery, UserResponse> {

    @Override
    public UserResponse handle(Request query) {
        // Starter stub: replace with read model lookup.
        return null;
    }

    @Override
    public Http getRequestType() {
        return Http.GET;
    }

    @Override
    public Class<GetUserQuery> getCqrsMessage() {
        return GetUserQuery.class;
    }

    @Override
    public Class<UserResponse> getResponseMessage() {
        return UserResponse.class;
    }


    @Override
    public Response handle(Query<? extends Message> message) {
        return null;
    }
}
