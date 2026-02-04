package com.app.springsandboxone.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Album(
    @JsonProperty("id") Integer id,
    @JsonProperty("userId") Integer userId,
    @JsonProperty("title") String title
) {}
