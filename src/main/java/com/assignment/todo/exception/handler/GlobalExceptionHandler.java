package com.assignment.todo.exception.handler;

import com.assignment.todo.dto.ErrorResponse;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global Exception Handler
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            final ConstraintViolationException ex, final HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> {
            String path = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            errors.add(path + ": '" + message + "'");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .path(request.getRequestURI())
                        .message("Invalid Request")
                        .messages(errors)
                        .build());
    }

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleRequestValidationException(
            final MethodArgumentNotValidException exception, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .path(request.getRequestURI())
                        .message("Invalid Request")
                        .messages(MethodArgumentNotValidException.errorsToStringList(exception.getAllErrors()))
                        .build());
    }

    @ExceptionHandler(value = {
            ItemNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            final Exception exception, final HttpServletRequest request) {
        var requestUri = request.getRequestURI();
        // Depending on the possible use cases, we can skip printing the stacktrace here
        log.error("Exception encountered in path {} : {}", requestUri , exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .path(requestUri)
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(value = {
            ActionNotAllowedException.class
    })
    public ResponseEntity<ErrorResponse> handleNotAllowedException(
            final Exception exception, final HttpServletRequest request) {
        var requestUri = request.getRequestURI();
        log.error("Exception encountered in path {} : {}", requestUri , exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .path(requestUri)
                        .message(exception.getMessage())
                        .build());
    }

}
