package dev.playerblair.kuro.collection;

import dev.playerblair.kuro.ErrorResponse;
import dev.playerblair.kuro.collection.exception.CollectionEntryAlreadyExistsException;
import dev.playerblair.kuro.collection.exception.CollectionEntryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CollectionEntryExceptionHandler {
    @ExceptionHandler(CollectionEntryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCollectionEntryNotFoundException(CollectionEntryNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(CollectionEntryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCollectionEntryAlreadyExistsException
            (CollectionEntryAlreadyExistsException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
