package com.example.common.dto;

import java.util.UUID;

import com.example.common.dao.UserDao;

public record UserDto(UUID id, String email) {
    public static UserDto toUserDao(UserDao user) {
        return new UserDto(user.id(), user.email());
    }
}
