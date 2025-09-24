package dev.playerblair.kuro.exception;

import lombok.Getter;

@Getter
public class JikanApiException extends RuntimeException {
    private final int statusCode;

    public JikanApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
