package com.example.api.interfaces.rest;

import com.example.api.application.messages.CreateUserCommand;
import com.example.api.application.messages.GetUserQuery;
import com.example.api.application.messages.UserResponse;
import com.example.common.cqrs.bus.CqrsBus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final CqrsBus bus;

    public UserController(CqrsBus bus) {
        this.bus = bus;
    }

    @PostMapping
    @Operation(summary = "Create user (command)")
    public UserResponse create(@RequestBody CreateUserCommand command) {
        return bus.dispatch(command);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user (query)")
    public UserResponse get(@PathVariable UUID id, @RequestParam String tenantId) {
        return bus.dispatch(new GetUserQuery(tenantId, id));
    }
}
