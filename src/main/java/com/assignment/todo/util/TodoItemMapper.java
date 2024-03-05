package com.assignment.todo.util;

import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItem;

public class TodoItemMapper {

    /**
     * Map TodoItem Entity to DTO
     *
     * @param entity  {@link TodoItemEntity} to map
     * @return {@link TodoItem}
     */
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
