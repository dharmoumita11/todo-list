package com.assignment.todo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private String path;
    private String message;
    private List<String> messages;

}
