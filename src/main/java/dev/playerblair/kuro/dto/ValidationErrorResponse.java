package dev.playerblair.kuro.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse<T>(
        int status,
        Map<String, T> errors,
        LocalDateTime timestamp
) {
}
