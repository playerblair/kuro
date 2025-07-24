package dev.playerblair.kuro.manga;

import dev.playerblair.kuro.ErrorResponse;
import dev.playerblair.kuro.manga.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class MangaExceptionHandler {

    @ExceptionHandler(JikanApiMangaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJikanApiMangaNotFoundException
            (JikanApiMangaNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(JikanApiRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleJikanApiRateLimitException(JikanApiRateLimitException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                429,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    @ExceptionHandler(JikanApiException.class)
    public ResponseEntity<ErrorResponse> handleJikanApiException(JikanApiException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(JikanApiInternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleJikanApiInternalServerErrorException
            (JikanApiInternalServerErrorException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(JikanApiServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleJikanApiServiceUnavailableException
            (JikanApiServiceUnavailableException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                503,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(MangaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMangaNotFoundException(MangaNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
