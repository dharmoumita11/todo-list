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

    /**
     * Get all the TodoItems
     *
     * @param includeAll  fetch all the TodoItems if set to true,
     *                    otherwise fetch the TodoItems which are not done by default
     * @return List of {@link TodoItemEntity}
     */
    @Override
    public List<TodoItemEntity> getAllItems(final boolean includeAll) {
        if (includeAll) {
            return todoItemEntityRepository.findAll();
        } else {
            return todoItemEntityRepository.findAllByStatusNot(TodoItemStatus.DONE.name());
        }
    }

    /**
     * Get the details of a TodoItem
     *
     * @param id  ID of the TodoItem
     * @return {@link TodoItemEntity} for the input ID
     */
    @Override
    public TodoItemEntity getItemDetails(Integer id) {
        return todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
    }

    /**
     * Add a new TodoItem to the list
     *
     * @param item  {@link TodoItemRequest}
     * @return newly added {@link TodoItemEntity}
     */
    @Override
    public TodoItemEntity addItem(TodoItemRequest item) {
        return todoItemEntityRepository.save(
                TodoItemEntity.builder()
                        .description(item.getDescription())
                        .dueDateTime(item.getDueDateTime())
                        .status(TodoItemStatus.NOT_DONE.name())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
    }

    /**
     * Update a TodoItem in the list
     *
     * @param item  {@link TodoItemRequest}
     * @return updated {@link TodoItemEntity}
     */
    @Override
    public TodoItemEntity updateItem(Integer id, TodoItemRequest item) {
        TodoItemEntity itemEntity = todoItemEntityRepository.findById(id)
                // TODO : Momo : ItemNotFoundException
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
        // Don't allow updates on PAST_DUE items
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

    /**
     * Mark a TodoItem as 'DONE'
     *
     * @param id  ID of the TodoItem
     * @return updated {@link TodoItemEntity}
     */
    @Override
    public TodoItemEntity markAsDone(Integer id) {
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
        item.setStatus(TodoItemStatus.DONE.name());
        item.setDoneAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return todoItemEntityRepository.save(item);
    }

    /**
     * Mark a TodoItem as 'NOT DONE'
     *
     * @param id  ID of the TodoItem
     * @return updated {@link TodoItemEntity}
     */
    @Override
    public TodoItemEntity markAsNotDone(Integer id) {
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to-do item ID: " + id));
        // Don't allow PAST_DUE items to be marked as NOT_DONE
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

    /**
     * Delete a TodoItem
     *
     * @param id  ID of the TodoItem
     */
    @Override
    public void deleteItem(Integer id) {
        todoItemEntityRepository.deleteById(id);
    }

    /**
     * Check for Due Date of TodoItems with status other than 'DONE'
     * if it's past their due date, update their status to 'PAST DUE'
     */
    @Override
    public void checkAndUpdateStatusForPastDueItems() {
        // If more statuses are introduced,
        // the query might benefit from status not in (done, past_due) or a status in (not_done, etc)
        List<TodoItemEntity> items = todoItemEntityRepository.findAllByStatusAndDueDateTimeLessThan(
                TodoItemStatus.NOT_DONE.name(), LocalDateTime.now());
        items.forEach(item -> {
            item.setStatus(TodoItemStatus.PAST_DUE.name());
            item.setUpdatedAt(LocalDateTime.now());
        });
        todoItemEntityRepository.saveAll(items);
    }

}
