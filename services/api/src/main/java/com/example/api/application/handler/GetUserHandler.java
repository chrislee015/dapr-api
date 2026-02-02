package com.example.api.application.handler;

import com.example.api.application.messages.GetUserQuery;
import com.example.api.application.messages.UserResponse;
import com.example.common.cqrs.handler.QueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetUserHandler implements QueryHandler<GetUserQuery, UserResponse> {

    @Override
    public UserResponse handle(GetUserQuery query) {
        // Starter stub: replace with read model lookup.
        return new UserResponse(query.id(), "user@example.com");
    }
}
