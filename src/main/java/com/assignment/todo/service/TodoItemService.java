package com.assignment.todo.service;

import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItemRequest;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;

import java.util.List;

public interface TodoItemService {

    /**
     * Get all the TodoItems
     *
     * @param includeAll  fetch all the TodoItems if set to true,
     *                    otherwise fetch the TodoItems which are not done by default
     * @return List of {@link TodoItemEntity}
     */
    List<TodoItemEntity> getAllItems(boolean includeAll);

    /**
     * Get the details of a TodoItem
     *
     * @param id  ID of the TodoItem
     * @return {@link TodoItemEntity} for the input ID
     */
    TodoItemEntity getItemDetails(Integer id) throws ItemNotFoundException;

    /**
     * Add a new TodoItem to the list
     *
     * @param item  {@link TodoItemRequest}
     * @return newly added {@link TodoItemEntity}
     */
    TodoItemEntity addItem(TodoItemRequest item);

    /**
     * Update a TodoItem in the list
     *
     * @param item  {@link TodoItemRequest}
     * @return updated {@link TodoItemEntity}
     */
    TodoItemEntity updateItem(Integer id, TodoItemRequest item) throws ItemNotFoundException, ActionNotAllowedException;

    /**
     * Mark a TodoItem as 'DONE'
     *
     * @param id  ID of the TodoItem
     * @return updated {@link TodoItemEntity}
     */
    TodoItemEntity markAsDone(Integer id) throws ItemNotFoundException;

    /**
     * Mark a TodoItem as 'NOT DONE'
     *
     * @param id  ID of the TodoItem
     * @return updated {@link TodoItemEntity}
     */
    TodoItemEntity markAsNotDone(Integer id) throws ItemNotFoundException, ActionNotAllowedException;

    /**
     * Delete a TodoItem
     *
     * @param id  ID of the TodoItem
     */
    void deleteItem(Integer id);

    /**
     * Check for Due Date of TodoItems with status other than 'DONE'
     * if it's past their due date, update their status to 'PAST DUE'
     */
    void checkAndUpdateStatusForPastDueItems();

}
