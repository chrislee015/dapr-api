package com.app.springsandboxone.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Todo(
    @JsonProperty("id") Integer id,
    @JsonProperty("userId") Integer userId,
    @JsonProperty("title") String title,
    @JsonProperty("completed") Boolean completed
) {}
