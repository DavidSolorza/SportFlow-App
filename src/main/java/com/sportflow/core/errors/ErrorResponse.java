package com.sportflow.core.errors;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<ValidationErrorDetail> details
) {
    public record ValidationErrorDetail(String field, String issue) {}

    public static ErrorResponse of(int status, String error, String code, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, code, message, path, List.of());
    }

    public static ErrorResponse of(int status, String error, String code, String message, String path, List<ValidationErrorDetail> details) {
        return new ErrorResponse(Instant.now(), status, error, code, message, path, details != null ? details : List.of());
    }
}
