package dev.playerblair.kuro.handler;

import dev.playerblair.kuro.dto.ErrorResponse;
import dev.playerblair.kuro.exception.CollectionEntryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CollectionExceptionHandler {

    @ExceptionHandler(CollectionEntryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCollectionEntryNotFoundException(CollectionEntryNotFoundException exception) {
        ErrorResponse response = new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
