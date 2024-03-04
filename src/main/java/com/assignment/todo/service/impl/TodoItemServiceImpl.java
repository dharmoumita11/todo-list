package com.assignment.todo.service.impl;

import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.dao.TodoItemEntityRepository;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItemRequest;
import com.assignment.todo.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoItemServiceImpl implements TodoItemService {

    private final TodoItemEntityRepository todoItemEntityRepository;

    @Autowired
    public TodoItemServiceImpl(TodoItemEntityRepository todoItemEntityRepository) {
        this.todoItemEntityRepository = todoItemEntityRepository;
    }

    @Override
    public List<TodoItemEntity> getAllItems(final boolean includeAll) {
        if (includeAll) {
            return todoItemEntityRepository.findAll();
        } else {
            return todoItemEntityRepository.findAllByStatusNot(TodoItemStatus.DONE.name());
        }
    }

    @Override
    public TodoItemEntity getItemDetails(Integer id) {
        return todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
    }

    @Override
    public TodoItemEntity addItem(TodoItemRequest item) {
        return todoItemEntityRepository.save(
                TodoItemEntity.builder()
                        .description(item.getDescription())
                        .dueDateTime(item.getDueDateTime())
                        .status(TodoItemStatus.NOT_DONE.value())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
    }

    @Override
    public TodoItemEntity updateItem(Integer id, TodoItemRequest item) {
        TodoItemEntity itemEntity = todoItemEntityRepository.findById(id)
                // TODO : Momo : ItemNotFoundException
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
        if (TodoItemStatus.PAST_DUE.name().contentEquals(itemEntity.getStatus())) {
            // log error
            // throw custom not allowed
            throw new RuntimeException();
        }
        itemEntity.setDescription(item.getDescription());
        itemEntity.setDueDateTime(item.getDueDateTime());
        itemEntity.setUpdatedAt(LocalDateTime.now());
        return todoItemEntityRepository.save(itemEntity);
    }

    @Override
    public TodoItemEntity markAsDone(Integer id) {
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
        item.setStatus(TodoItemStatus.DONE.name());
        item.setDoneAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return todoItemEntityRepository.save(item);
    }

    @Override
    public TodoItemEntity markAsNotDone(Integer id) {
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
        if (TodoItemStatus.PAST_DUE.name().contentEquals(item.getStatus())) {
            // log error
            // throw custom not allowed
            throw new RuntimeException();
        }
        item.setStatus(TodoItemStatus.NOT_DONE.name());
        item.setDoneAt(null); // Clear the done date-time
        item.setUpdatedAt(LocalDateTime.now());
        return todoItemEntityRepository.save(item);
    }

    @Override
    public void checkAndUpdateStatusForPastDueItems() {
        List<TodoItemEntity> items = todoItemEntityRepository.findAllByStatusAndDueDateTimeLessThan(
                TodoItemStatus.NOT_DONE.name(), LocalDateTime.now());
        items.forEach(item -> {
            item.setStatus(TodoItemStatus.PAST_DUE.name());
            item.setUpdatedAt(LocalDateTime.now());
        });
        todoItemEntityRepository.saveAll(items);
    }

    @Override
    public void deleteItem(Integer id) {
        // allow deletion of past due items???
        todoItemEntityRepository.deleteById(id);
    }
}
