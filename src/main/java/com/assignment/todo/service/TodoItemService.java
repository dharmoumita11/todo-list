package com.assignment.todo.service;

import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItemRequest;

import java.util.List;

public interface TodoItemService {

    List<TodoItemEntity> getAllItems(boolean includeAll);
    TodoItemEntity getItemDetails(Integer id);
    TodoItemEntity addItem(TodoItemRequest item);
    TodoItemEntity updateItem(Integer id, TodoItemRequest item);
    TodoItemEntity markAsDone(Integer id);
    TodoItemEntity markAsNotDone(Integer id);
    void checkAndUpdateStatusForPastDueItems();

    void deleteItem(Integer id);
}
