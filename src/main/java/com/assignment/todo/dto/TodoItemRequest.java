package com.assignment.todo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TodoItemRequest {
    private String description;
    private LocalDateTime dueDateTime;
}
