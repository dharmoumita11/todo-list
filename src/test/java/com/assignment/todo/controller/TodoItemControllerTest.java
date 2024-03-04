package com.assignment.todo.controller;

import com.assignment.todo.BaseTestClass;
import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItem;
import com.assignment.todo.dto.TodoItemRequest;
import com.assignment.todo.service.impl.TodoItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class TodoItemControllerTest extends BaseTestClass {

    @Mock
    private TodoItemServiceImpl todoItemService;

    @InjectMocks
    private TodoItemController todoItemController;

    @Test
    void whenGetAllTodoItems_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .description("Test Item")
                .status("NOT_DONE")
                .build();
        when(todoItemService.getAllItems(true)).thenReturn(Collections.singletonList(mockItem));
        List<TodoItem> newItems = todoItemController.getAllNotDoneItems(true);
        assertThat(newItems.size()).isEqualTo(1);
        assertThat(newItems.get(0).getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenGetNotDoneTodoItems_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .description("Test Item")
                .status("NOT_DONE")
                .build();
        when(todoItemService.getAllItems(false)).thenReturn(Collections.singletonList(mockItem));
        List<TodoItem> newItems = todoItemController.getAllNotDoneItems(false);
        assertThat(newItems.size()).isEqualTo(1);
        assertThat(newItems.get(0).getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenGetTodoItemId_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status("NOT_DONE")
                .build();
        when(todoItemService.getItemDetails(1)).thenReturn(mockItem);
        TodoItem newItem = todoItemController.getTodoItemDetails(1);
        assertThat(newItem.getId()).isEqualTo(1);
    }

    @Test
    void whenAddTodoItem_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .description("Test Item")
                .status("NOT_DONE")
                .build();
        when(todoItemService.addItem(any(TodoItemRequest.class))).thenReturn(mockItem);
        TodoItem newItem = todoItemController.addTodoItem(TodoItemRequest.builder().build());
        assertThat(newItem.getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenUpdateTodoItem_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status("NOT_DONE")
                .build();
        when(todoItemService.updateItem(anyInt(), any(TodoItemRequest.class))).thenReturn(mockItem);
        TodoItem newItem = todoItemController.updateTodoItem(1, TodoItemRequest.builder().build());
        assertThat(newItem.getId()).isEqualTo(1);
        assertThat(newItem.getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenMarkTodoItemAsDone_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status("DONE")
                .build();
        when(todoItemService.markAsDone(1)).thenReturn(mockItem);
        TodoItem newItem = todoItemController.markAsDone(1);
        assertThat(newItem.getId()).isEqualTo(1);
        assertThat(newItem.getStatus()).isEqualTo(TodoItemStatus.DONE.value());
    }

    @Test
    void whenMarkTodoItemAsNotDone_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status("NOT_DONE")
                .build();
        when(todoItemService.markAsNotDone(1)).thenReturn(mockItem);
        TodoItem newItem = todoItemController.markAsNotDone(1);
        assertThat(newItem.getId()).isEqualTo(1);
        assertThat(newItem.getStatus()).isEqualTo(TodoItemStatus.NOT_DONE.value());
    }

    @Test
    void whenDeleteTodoItem() {
        todoItemController.deleteTodoItem(1);
        verify(todoItemService).deleteItem(1);
    }

}
