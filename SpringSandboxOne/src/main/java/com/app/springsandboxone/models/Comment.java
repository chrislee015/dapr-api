package com.app.springsandboxone.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Comment(
    @JsonProperty("id") Integer id,
    @JsonProperty("postId") Integer postId,
    @JsonProperty("name") String name,
    @JsonProperty("email") String email,
    @JsonProperty("body") String body
) {}
