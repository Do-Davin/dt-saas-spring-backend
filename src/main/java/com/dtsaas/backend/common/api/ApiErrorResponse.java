package com.dtsaas.backend.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        int status,
        String message,
        String path,
        Instant timestamp,
        Map<String, String> errors) {

    public static ApiErrorResponse of(int status, String message, String path) {
        return new ApiErrorResponse(status, message, path, Instant.now(), null);
    }

    public static ApiErrorResponse of(int status, String message, String path, Map<String, String> errors) {
        return new ApiErrorResponse(status, message, path, Instant.now(), errors);
    }
}
