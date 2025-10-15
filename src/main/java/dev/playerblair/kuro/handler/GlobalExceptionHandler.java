package dev.playerblair.kuro.handler;

import dev.playerblair.kuro.dto.ErrorResponse;
import dev.playerblair.kuro.dto.ValidationErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ValidationErrorResponse<List<String>>> handleHandlerMethodValidationException
            (HandlerMethodValidationException exception) {
        Map<String, List<String>> errors = new HashMap<>();

        exception.getParameterValidationResults().forEach(error -> {
            String field = error.getMethodParameter().getParameterName();
            List<String> messages = error.getResolvableErrors().stream()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .toList();
            errors.put(field, messages);
        });

        ValidationErrorResponse<List<String>> response = new ValidationErrorResponse<>(
                400,
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse<String>> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        ValidationErrorResponse<String> response = new ValidationErrorResponse<>(
                400,
                errors,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException
            (HttpMessageNotReadableException exception) {
        ErrorResponse response = new ErrorResponse(
                400,
                "The request body could not be parsed. Please check your JSON syntax and field types.",
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse<String>> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String field = path.substring(path.lastIndexOf(".") + 1);
            String message = violation.getMessage();
            errors.put(field, message);
        });

        ValidationErrorResponse<String> response = new ValidationErrorResponse<>(
                400,
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }
}
