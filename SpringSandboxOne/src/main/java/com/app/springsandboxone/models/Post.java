package com.app.springsandboxone.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Post(
    @JsonProperty("id") Integer id,
    @JsonProperty("userId") Integer userId,
    @JsonProperty("title") String title,
    @JsonProperty("body") String body
) {}
