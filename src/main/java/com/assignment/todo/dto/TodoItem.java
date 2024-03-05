package com.assignment.todo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Representation of a TodoItem
 */
@Data
@Builder
public class TodoItem {
    private Integer id;
    private String description;
    private String status; // "not done", "done", "past due"
    private LocalDateTime dueDateTime;
    private LocalDateTime doneAt;
    private LocalDateTime createdAt;
}
