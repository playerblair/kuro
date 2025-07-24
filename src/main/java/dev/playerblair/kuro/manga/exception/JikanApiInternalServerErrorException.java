package dev.playerblair.kuro.manga.exception;

public class JikanApiInternalServerErrorException extends RuntimeException {
    public JikanApiInternalServerErrorException(String message) {
        super(message);
    }
}
