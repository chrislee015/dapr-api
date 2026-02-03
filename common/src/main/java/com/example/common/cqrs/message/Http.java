package com.example.common.cqrs.message;

public enum Http {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS;

    public String getSimpleName() {
    return this.name();
    }
}
