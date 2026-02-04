package com.app.springsandboxone.models;

import java.time.LocalDateTime;

public record TaskResult<T>(
    String containerName,
    String endpoint,
    String method,
    int httpStatusCode,
    boolean successful,
    T data,
    String errorMessage,
    LocalDateTime timestamp,
    long executionTimeMs
) {
    public static <T> TaskResult<T> success(String containerName, String endpoint, String method,
                                           int httpStatusCode, T data, long executionTimeMs) {
        return new TaskResult<>(containerName, endpoint, method, httpStatusCode, true, data,
                               null, LocalDateTime.now(), executionTimeMs);
    }

    public static <T> TaskResult<T> error(String containerName, String endpoint, String method,
                                         int httpStatusCode, String errorMessage, long executionTimeMs) {
        return new TaskResult<>(containerName, endpoint, method, httpStatusCode, false, null,
                               errorMessage, LocalDateTime.now(), executionTimeMs);
    }
}
