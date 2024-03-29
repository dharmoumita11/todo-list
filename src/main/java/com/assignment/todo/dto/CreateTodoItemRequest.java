package com.assignment.todo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represent a request body for creating a TodoItem
 */
@Data
@Builder
public class CreateTodoItemRequest {

    @NotBlank
    private String description;

    @NotNull
    @Future
    private LocalDateTime dueDateTime;

}
