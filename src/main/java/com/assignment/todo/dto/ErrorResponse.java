package com.assignment.todo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private String path;
    private String message;

}
