package dev.playerblair.kuro.handler;

import dev.playerblair.kuro.dto.ErrorResponse;
import dev.playerblair.kuro.exception.JikanApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class JikanExceptionHandler {

    @ExceptionHandler(JikanApiException.class)
    public ResponseEntity<ErrorResponse> handleJikanApiException(JikanApiException exception) {
        ErrorResponse response = new ErrorResponse(
                exception.getStatusCode(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.valueOf(response.status())).body(response);
    }
}
