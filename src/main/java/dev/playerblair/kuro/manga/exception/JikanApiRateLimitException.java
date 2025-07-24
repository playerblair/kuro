package dev.playerblair.kuro.manga.exception;

public class JikanApiRateLimitException extends RuntimeException {
  public JikanApiRateLimitException(String message) {
    super(message);
  }
}
