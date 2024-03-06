package com.assignment.todo.service;

import com.assignment.todo.BaseTestClass;
import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.dao.TodoItemEntityRepository;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.CreateTodoItemRequest;
import com.assignment.todo.dto.UpdateTodoItemRequest;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import com.assignment.todo.service.impl.TodoItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TodoItemServiceTest extends BaseTestClass {

    @Mock
    private TodoItemEntityRepository todoItemRepository;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    @Test
    void whenGetAllItems_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").build();
        when(todoItemRepository.findAll()).thenReturn(List.of(mockItem));

        List<TodoItemEntity> newItems = todoItemService.getAllItems(true);

        assertThat(newItems.size()).isEqualTo(1);
        assertThat(newItems.get(0).getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenGetNotDoneItems_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").build();
        when(todoItemRepository.findAllByStatusNot(TodoItemStatus.DONE.name())).thenReturn(List.of(mockItem));

        List<TodoItemEntity> newItems = todoItemService.getAllItems(false);

        assertThat(newItems.size()).isEqualTo(1);
        assertThat(newItems.get(0).getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenGetItemById_thenSuccess() throws ItemNotFoundException {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        TodoItemEntity newItem = todoItemService.getItemDetails(1);

        assertThat(newItem.getId()).isEqualTo(1);
        assertThat(newItem.getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenGetItemByInvalidId_thenItemNotFound() {
        when(todoItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoItemService.getItemDetails(1))
                .isInstanceOf(ItemNotFoundException.class).hasMessageContaining("Item id 1");
    }

    @Test
    void whenAddItem_thenSuccess() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .description("Test Item").build();
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.addItem(CreateTodoItemRequest.builder().build());

        assertThat(newItem.getDescription()).isEqualTo("Test Item");
    }

    @Test
    void whenUpdateItemDescription_thenSuccess() throws ItemNotFoundException, ActionNotAllowedException {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .dueDateTime(LocalDateTime.of(2030, 12, 31, 14, 15))
                .updatedAt(LocalDateTime.of(2023, 12, 31, 14, 15))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.updateItem(1, UpdateTodoItemRequest.builder()
                .description("Update Item").build());

        assertThat(newItem.getDescription()).isEqualTo("Update Item");
        assertThat(newItem.getDueDateTime()).isEqualTo(LocalDateTime.of(2030, 12, 31, 14, 15));
        assertThat(newItem.getUpdatedAt()).isNotEqualTo(LocalDateTime.of(2023, 12, 31, 14, 15));
    }

    @Test
    void whenUpdateItemDueDate_thenSuccess() throws ItemNotFoundException, ActionNotAllowedException {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .dueDateTime(LocalDateTime.now().plusDays(1))
                .updatedAt(LocalDateTime.of(2023, 12, 31, 14, 15))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.updateItem(
                1, UpdateTodoItemRequest.builder().dueDateTime(LocalDateTime.of(2030, 12, 31, 14, 15)).build());

        assertThat(newItem.getDescription()).isEqualTo("Test Item");
        assertThat(newItem.getDueDateTime()).isEqualTo(LocalDateTime.of(2030, 12, 31, 14, 15));
        assertThat(newItem.getUpdatedAt()).isNotEqualTo(LocalDateTime.of(2023, 12, 31, 14, 15));
    }

    @Test
    void whenUpdateItemNoData_thenUpdateNothing() throws ItemNotFoundException, ActionNotAllowedException {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .dueDateTime(LocalDateTime.now().plusDays(1))
                .updatedAt(LocalDateTime.of(2023, 12, 31, 14, 15))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.updateItem(
                1, UpdateTodoItemRequest.builder().build());

        assertThat(newItem.getDescription()).isEqualTo("Test Item");
        assertThat(newItem.getDueDateTime()).isEqualTo(mockItem.getDueDateTime());
        assertThat(newItem.getUpdatedAt()).isEqualTo(LocalDateTime.of(2023, 12, 31, 14, 15));
    }

    @Test
    void whenUpdateInvalidItem_thenItemNotFound() {
        when(todoItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoItemService.updateItem(1, UpdateTodoItemRequest.builder().build()))
                .isInstanceOf(ItemNotFoundException.class).hasMessageContaining("Item id 1");
    }

    @Test
    void whenUpdatePastDueItem_thenActionNotAllowed() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").status(TodoItemStatus.PAST_DUE.name()).build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        assertThatThrownBy(() -> todoItemService.updateItem(1, UpdateTodoItemRequest.builder().build()))
                .isInstanceOf(ActionNotAllowedException.class).hasMessageContaining("Todo item with id 1");
    }

    @Test
    void whenUpdate30SecondPastDueItem_thenActionNotAllowed() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .dueDateTime(LocalDateTime.now().minusSeconds(30))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        assertThatThrownBy(() -> todoItemService.updateItem(1, UpdateTodoItemRequest.builder().build()))
                .isInstanceOf(ActionNotAllowedException.class).hasMessageContaining("Todo item with id 1");
    }

    @Test
    void whenUpdateDoneItem_thenActionNotAllowed() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.DONE.name())
                .dueDateTime(LocalDateTime.of(2023, 12, 31, 14, 15))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        assertThatThrownBy(() -> todoItemService.updateItem(1, UpdateTodoItemRequest.builder().build()))
                .isInstanceOf(ActionNotAllowedException.class).hasMessageContaining("Todo item with id 1");
    }

    @Test
    void whenMarkItemAsDone_thenSuccess() throws ItemNotFoundException {
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
    void whenMarkDoneItemAsDone_thenSuccess() throws ItemNotFoundException {
        final TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").status(TodoItemStatus.DONE.name()).build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        todoItemService.markAsDone(1);

        verify(todoItemRepository, never()).save(mockItem);
    }

    @Test
    void whenMarkInvalidItemAsDone_thenItemNotFound() {
        when(todoItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoItemService.markAsDone(1))
                .isInstanceOf(ItemNotFoundException.class).hasMessageContaining("Item id 1");
    }

    @Test
    void whenMarkItemAsNotDone_thenSuccess() throws ItemNotFoundException, ActionNotAllowedException {
        final TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.DONE.name())
                .doneAt(LocalDateTime.now())
                .dueDateTime(LocalDateTime.of(2050, 12, 31, 14, 15))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));
        when(todoItemRepository.save(any(TodoItemEntity.class))).thenReturn(mockItem);

        TodoItemEntity newItem = todoItemService.markAsNotDone(1);

        assertThat(newItem.getStatus()).isEqualTo(TodoItemStatus.NOT_DONE.name());
        assertThat(newItem.getDoneAt()).isNull();
        assertThat(newItem.getUpdatedAt()).isEqualTo(mockItem.getUpdatedAt());
    }

    @Test
    void whenMarkNotDoneItemAsNotDone_thenDoNothing() throws ItemNotFoundException, ActionNotAllowedException {
        final TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .doneAt(LocalDateTime.now())
                .dueDateTime(LocalDateTime.of(2050, 12, 31, 14, 15))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        todoItemService.markAsNotDone(1);

        verify(todoItemRepository, never()).save(mockItem);
    }

    @Test
    void whenMarkInvalidItemAsNotDone_thenItemNotFound() {
        when(todoItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoItemService.markAsNotDone(1))
                .isInstanceOf(ItemNotFoundException.class).hasMessageContaining("Item id 1");
    }

    @Test
    void whenMarkPastDueItemAsNotDone_thenActionNotAllowed() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1).description("Test Item").status(TodoItemStatus.PAST_DUE.name()).build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        assertThatThrownBy(() -> todoItemService.markAsNotDone(1))
                .isInstanceOf(ActionNotAllowedException.class).hasMessageContaining("Todo item with id 1");
    }

    @Test
    void whenMark30SecondPastDueItemAsNotDone_thenActionNotAllowed() {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .dueDateTime(LocalDateTime.now().minusSeconds(30))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        assertThatThrownBy(() -> todoItemService.markAsNotDone(1))
                .isInstanceOf(ActionNotAllowedException.class).hasMessageContaining("Todo item with id 1");
    }

    @Test
    void whenDeleteItem_thenSuccess() throws ItemNotFoundException {
        final TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .doneAt(LocalDateTime.now())
                .dueDateTime(LocalDateTime.of(2050, 12, 31, 14, 15))
                .build();
        when(todoItemRepository.findById(1)).thenReturn(Optional.of(mockItem));

        todoItemService.deleteItem(1);

        verify(todoItemRepository, times(1)).deleteById(1);
    }

    @Test
    void whenDeleteItem_thenItemNotFound() {
        when(todoItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoItemService.deleteItem(1))
                .isInstanceOf(ItemNotFoundException.class).hasMessageContaining("Item id 1");
    }

    @Test
    void whenCheckAndUpdateStatusForPastDueItems_thenUpdateSuccess() {
        final TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Test Item")
                .status(TodoItemStatus.NOT_DONE.name())
                .doneAt(LocalDateTime.now())
                .dueDateTime(LocalDateTime.of(2024, 3, 5, 14, 15))
                .build();
        when(todoItemRepository.findAllByStatusAndDueDateTimeLessThan(anyString(), any())).thenReturn(List.of(mockItem));

        todoItemService.checkAndUpdateStatusForPastDueItems();

        verify(todoItemRepository, times(1)).saveAll(any());
    }

    @Test
    void whenCheckAndUpdateStatusForPastDueItems_thenNothingToUpdate() {
        when(todoItemRepository.findAllByStatusAndDueDateTimeLessThan(anyString(), any())).thenReturn(List.of());

        todoItemService.checkAndUpdateStatusForPastDueItems();

        verify(todoItemRepository, never()).saveAll(any());
    }

}
