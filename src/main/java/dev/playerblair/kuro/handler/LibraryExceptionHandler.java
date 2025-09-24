package dev.playerblair.kuro.handler;

import dev.playerblair.kuro.dto.ErrorResponse;
import dev.playerblair.kuro.exception.LibraryEntryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class LibraryExceptionHandler {

    @ExceptionHandler(LibraryEntryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLibraryEntryNotFoundException(LibraryEntryNotFoundException exception) {
        ErrorResponse response = new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
