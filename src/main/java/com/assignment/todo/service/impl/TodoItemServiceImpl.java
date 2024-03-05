package com.assignment.todo.service.impl;

import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.dao.TodoItemEntityRepository;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItemRequest;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import com.assignment.todo.service.TodoItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
     * @return {@link TodoItemEntity} for the input id
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Override
    public TodoItemEntity getItemDetails(Integer id) throws ItemNotFoundException {
        return todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
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
     * @param request  {@link TodoItemRequest}
     * @return updated {@link TodoItemEntity}
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     * @throws ActionNotAllowedException if the item for the input id is past due
     */
    @Override
    public TodoItemEntity updateItem(Integer id, TodoItemRequest request) throws ItemNotFoundException, ActionNotAllowedException {
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                // TODO : Momo : ItemNotFoundException
                .orElseThrow(() -> new ItemNotFoundException(id));
        // Don't allow updates on PAST_DUE items
        if (TodoItemStatus.PAST_DUE.name().contentEquals(item.getStatus())) {
            log.error("Attempted to update a past due TodoItem id {} with due date {}", id, request.getDueDateTime());
            throw new ActionNotAllowedException("Updates on Todo item with id " + id + " is not allowed because it's past due");
        }
        item.setDescription(request.getDescription());
        item.setDueDateTime(request.getDueDateTime());
        item.setUpdatedAt(LocalDateTime.now());
        return todoItemEntityRepository.save(item);
    }

    /**
     * Mark a TodoItem as 'DONE'
     *
     * @param id  ID of the TodoItem
     * @return updated {@link TodoItemEntity}
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Override
    public TodoItemEntity markAsDone(Integer id) throws ItemNotFoundException {
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
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
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     * @throws ActionNotAllowedException if the item for the input id is past due
     */
    @Override
    public TodoItemEntity markAsNotDone(Integer id) throws ItemNotFoundException, ActionNotAllowedException {
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        // Don't allow PAST_DUE items to be marked as NOT_DONE
        if (TodoItemStatus.PAST_DUE.name().contentEquals(item.getStatus())) {
            log.error("Attempted to mark a past due TodoItem id {} with due date {} as NOT DONE", id, item.getDueDateTime());
            throw new ActionNotAllowedException("Todo item with id " + id + " can't be marked as NOT DONE because it's past due");
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
