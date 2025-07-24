package dev.playerblair.kuro.library;

import dev.playerblair.kuro.ErrorResponse;
import dev.playerblair.kuro.library.exception.MangaEntryAlreadyExistsException;
import dev.playerblair.kuro.library.exception.MangaEntryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class LibraryExceptionHandler {

    @ExceptionHandler(MangaEntryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMangaEntryNotFoundException(MangaEntryNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MangaEntryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMangaEntryAlreadyExistsException
            (MangaEntryAlreadyExistsException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
