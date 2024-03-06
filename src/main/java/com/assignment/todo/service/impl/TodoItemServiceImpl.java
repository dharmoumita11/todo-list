package com.assignment.todo.service.impl;

import com.assignment.todo.constants.TodoItemStatus;
import com.assignment.todo.dal.dao.TodoItemEntityRepository;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.CreateTodoItemRequest;
import com.assignment.todo.dto.UpdateTodoItemRequest;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import com.assignment.todo.service.TodoItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
@Service
public class TodoItemServiceImpl implements TodoItemService {

    /* Add condition to check for past due items.
     Check for due_date as well for items who went past due
     after the last scheduled job run (say 10 seconds before)
     Note: This is precautionary as currently, the input accepts
     */
    private static final Predicate<TodoItemEntity> isPastDueItem = item ->
            TodoItemStatus.PAST_DUE.name().contentEquals(item.getStatus())
                    || item.getDueDateTime().isBefore(LocalDateTime.now());

    private final TodoItemEntityRepository todoItemEntityRepository;

    @Autowired
    public TodoItemServiceImpl(TodoItemEntityRepository todoItemEntityRepository) {
        this.todoItemEntityRepository = todoItemEntityRepository;
    }

    /**
     * Get all pending TodoItems OR retrieve all items
     *
     * @param includeAll  fetch all the TodoItems if set to true,
     *                    otherwise fetch the TodoItems which are not done by default
     * @return List of {@link TodoItemEntity}
     */
    @Override
    public List<TodoItemEntity> getAllItems(final boolean includeAll) {
        log.info("Get All Items : {}", includeAll);
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
        log.info("Get details for item id {}", id);
        return todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * Add a new TodoItem to the list
     *
     * @param item  {@link CreateTodoItemRequest}
     * @return newly added {@link TodoItemEntity}
     */
    @Override
    public TodoItemEntity addItem(CreateTodoItemRequest item) {
        log.info("Add new item with description : {}", item.getDescription());
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
     * @param request  {@link UpdateTodoItemRequest}
     * @return updated {@link TodoItemEntity}
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     * @throws ActionNotAllowedException if the item for the input id is past due
     */
    @Override
    public TodoItemEntity updateItem(Integer id, UpdateTodoItemRequest request) throws ItemNotFoundException, ActionNotAllowedException {
        log.info("Update item id {}", id);
        TodoItemEntity item = todoItemEntityRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        // updates on PAST_DUE items not allowed
        if (isPastDueItem.test(item)) {
            log.error("Attempted to update a past due TodoItem id {} with due date {}", id, request.getDueDateTime());
            throw new ActionNotAllowedException("Updates on Todo item with id " + id + " is not allowed because it's past due");
        }
        boolean updated = false;
        if (StringUtils.hasText(request.getDescription())) {
            log.info("Updating description of item id {}", id);
            item.setDescription(request.getDescription());
            updated = true;
        }
        if (Objects.nonNull(request.getDueDateTime())) {
            log.info("Updating due date time of item id {}", id);
            item.setDueDateTime(request.getDueDateTime());
            updated = true;
        }
        if (updated) {
            item.setUpdatedAt(LocalDateTime.now());
            return todoItemEntityRepository.save(item);
        } else {
            log.info("Nothing to update for item id {}", id);
            // TODO: Add a constraint to the request to check
            //  if the request has any data to update, if not, return 400
            return item; // for now returning the item as fetched
        }
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
        if (!item.getStatus().contentEquals(TodoItemStatus.DONE.name())) {
            item.setStatus(TodoItemStatus.DONE.name());
            item.setDoneAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            log.info("Item id {} marked as DONE at {}", id, item.getDoneAt());
            return todoItemEntityRepository.save(item);
        } else {
            log.info("Item id {} was already marked as DONE ", id);
            // Return the item as fetched without performing any update
            return item;
        }
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
        if (isPastDueItem.test(item)) {
            log.error("Attempted to mark a past due TodoItem id {} with due date {} as NOT DONE", id, item.getDueDateTime());
            throw new ActionNotAllowedException("Todo item with id " + id + " can't be marked as NOT DONE because it's past due");
        }
        if (item.getStatus().contentEquals(TodoItemStatus.DONE.name())) {
            item.setStatus(TodoItemStatus.NOT_DONE.name());
            item.setDoneAt(null); // Clear the done date-time
            item.setUpdatedAt(LocalDateTime.now());
            log.info("Item id {} marked as NOT_DONE at {}", id, item.getUpdatedAt());
            return todoItemEntityRepository.save(item);
        } else {
            log.info("Item id {} with status {} cannot be marked as NOT_DONE ", id, item.getStatus());
            // Return the item as fetched without performing any update
            return item;
        }
    }

    /**
     * Delete a TodoItem
     *
     * @param id  ID of the TodoItem
     */
    @Override
    public void deleteItem(Integer id) {
        log.info("Deleting item id {}", id);
        todoItemEntityRepository.deleteById(id);
    }

    /**
     * Check for Due Date of TodoItems with status other than 'DONE'
     * if it's past their due date, update their status to 'PAST DUE'
     */
    @Override
    public void checkAndUpdateStatusForPastDueItems() {
        // TODO: If more statuses are introduced,
        //  the query might benefit from status not in (done, past_due) or a status in (not_done, etc)
        List<TodoItemEntity> items = todoItemEntityRepository.findAllByStatusAndDueDateTimeLessThan(
                TodoItemStatus.NOT_DONE.name(), LocalDateTime.now());
        items.forEach(item -> {
            item.setStatus(TodoItemStatus.PAST_DUE.name());
            item.setUpdatedAt(LocalDateTime.now());
        });
        // TODO: This log can be updated to print all the IDs
        log.info("Updating {} items to PAST_DUE", items.size());
        if (items.size() > 0) {
            todoItemEntityRepository.saveAll(items);
        }
    }

}
