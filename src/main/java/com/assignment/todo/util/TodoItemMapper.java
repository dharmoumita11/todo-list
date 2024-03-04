package com.assignment.todo.util;

import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItem;
import com.assignment.todo.dto.TodoItemRequest;

public class TodoItemMapper {

    public static TodoItemEntity toEntity(final TodoItemRequest item) {
        return TodoItemEntity.builder()
                .description(item.getDescription())
                .status(TodoItemStatus.NOT_DONE.name())
                .dueDateTime(item.getDueDateTime())
                .build();
    }

    public static TodoItem toDto(final TodoItemEntity entity) {
        return TodoItem.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .status(TodoItemStatus.valueOf(entity.getStatus()).value())
                .dueDateTime(entity.getDueDateTime())
                .doneAt(entity.getDoneAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }


}
