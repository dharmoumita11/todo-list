package com.assignment.todo.service;

import com.assignment.todo.BaseTestClass;
import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.dao.TodoItemEntityRepository;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItemRequest;
import com.assignment.todo.service.impl.TodoItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TodoItemServiceTest extends BaseTestClass {

    @Mock
    private TodoItemEntityRepository todoItemRepository;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    @Test
    void whenGetAllItems_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").build();
        when(todoItemRepository.findAll()).thenReturn(Collections.singletonList(mockItem));

        List<TodoItemEntity> newItems = todoItemService.getAllItems(true);

        assertThat(newItems.size()).isEqualTo(1);
        assertThat(newItems.get(0).getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenGetNotDoneItems_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").build();
        when(todoItemRepository.findAllByStatusNot(TodoItemStatus.DONE.name())).thenReturn(Collections.singletonList(mockItem));

        List<TodoItemEntity> newItems = todoItemService.getAllItems(false);

        assertThat(newItems.size()).isEqualTo(1);
        assertThat(newItems.get(0).getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenGetItemById_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        TodoItemEntity newItem = todoItemService.getItemDetails(1);

        assertThat(newItem.getId()).isEqualTo(1);
        assertThat(newItem.getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenAddItem_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .description("Test Item").build();
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.addItem(TodoItemRequest.builder().build());

        assertThat(newItem.getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenUpdateItem_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").status(TodoItemStatus.NOT_DONE.name()).build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.updateItem(1, TodoItemRequest.builder().description("Update Item").dueDateTime(LocalDateTime.now()).build());

        assertThat(newItem.getDescription()).isEqualTo("Update Item");
        assertThat(newItem.getDueDateTime()).isEqualTo(mockItem.getDueDateTime());
    }

    @Test
    void whenMarkItemAsDone_thenSuccess() {
        final TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").status(TodoItemStatus.NOT_DONE.name()).build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.markAsDone(1);

        assertThat(newItem.getStatus()).isEqualTo(TodoItemStatus.DONE.name());
        assertThat(newItem.getDoneAt()).isEqualTo(mockItem.getDoneAt());
        assertThat(newItem.getUpdatedAt()).isEqualTo(mockItem.getUpdatedAt());
    }

    @Test
    void whenMarkItemAsNotDone_thenSuccess() {
        final TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").status(TodoItemStatus.DONE.name()).doneAt(LocalDateTime.now()).build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.markAsNotDone(1);

        assertThat(newItem.getStatus()).isEqualTo(TodoItemStatus.NOT_DONE.name());
        assertThat(newItem.getDoneAt()).isNull();
        assertThat(newItem.getUpdatedAt()).isEqualTo(mockItem.getUpdatedAt());
    }

    @Test
    void whenDeleteItem() {
        todoItemService.deleteItem(1);
        verify(todoItemRepository).deleteById(1);
    }

}
