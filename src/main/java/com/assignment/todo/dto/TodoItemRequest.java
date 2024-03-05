package com.assignment.todo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represent a request body for updating a TodoItem
 */
@Data
@Builder
public class TodoItemRequest {
    private String description;
    private LocalDateTime dueDateTime;
}
