package dev.playerblair.kuro.manga.exception;

public class JikanApiServiceUnavailableException extends RuntimeException {
    public JikanApiServiceUnavailableException() {
        super("Jikan API service is temporarily unavailable. Please retry after a delay.");
    }
}
