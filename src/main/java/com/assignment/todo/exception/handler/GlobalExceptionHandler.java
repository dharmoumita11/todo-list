package com.assignment.todo.exception.handler;

import com.assignment.todo.dto.ErrorResponse;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Global Exception Handler
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
            ItemNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            final Exception exception, final WebRequest request) {
        var requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
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
            final Exception exception, final WebRequest request) {
        var requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.error("Exception encountered in path {} : {}", requestUri , exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .path(requestUri)
                        .message(exception.getMessage())
                        .build());
    }

}
