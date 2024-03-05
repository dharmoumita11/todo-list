package com.assignment.todo.controller;

import com.assignment.todo.dto.TodoItem;
import com.assignment.todo.dto.TodoItemRequest;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import com.assignment.todo.service.TodoItemService;
import com.assignment.todo.util.TodoItemMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/todos")
public class TodoItemController {

    private final TodoItemService todoItemService;

    @Autowired
    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * Get all the TodoItems
     *
     * @param includeAll  fetch all the TodoItems if set to true,
     *                    otherwise fetch the TodoItems which are not done by default
     *
     * @return List of {@link TodoItem}
     */
    @Operation(summary = "Get all those TodoItems that are not DONE")
    @GetMapping
    public List<TodoItem> getAllNotDoneItems(
            @Parameter(description = "- true, get all the TodoItems \n" +
                    "- false, get all the TodoItems which are not done")
            @RequestParam(required = false, defaultValue = "false") boolean includeAll) {
        return todoItemService.getAllItems(includeAll).stream()
                .map(TodoItemMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get the details of a TodoItem
     *
     * @param id  ID of the TodoItem
     * @return {@link TodoItem} for the input ID
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Operation(summary = "Get the details of a TodoItem",
            responses = {
            @ApiResponse(responseCode = "200", description = "Found the TodoItem"),
            @ApiResponse(responseCode = "404", description = "TodoItem not found") })
    @GetMapping("/{id}")
    public TodoItem getTodoItemDetails(
            @Parameter(description = "ID of the TodoItem") @PathVariable Integer id)
            throws ItemNotFoundException {
        return TodoItemMapper.toDto(todoItemService.getItemDetails(id));
    }

    /**
     * Add a new TodoItem to the list
     *
     * @param item  {@link TodoItemRequest}
     * @return newly added {@link TodoItem}
     */
    @Operation(summary = "Add a new TodoItem to the list")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoItem addTodoItem(
            @RequestBody TodoItemRequest item) {
        return TodoItemMapper.toDto(todoItemService.addItem(item));
    }

    /**
     * Update a TodoItem in the list
     *
     * @param item  {@link TodoItemRequest}
     * @return updated {@link TodoItem}
     * @throws ActionNotAllowedException if the item for the input id is past due
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Operation(summary = "Update a TodoItem in the list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated the TodoItem"),
                    @ApiResponse(responseCode = "404", description = "TodoItem not found"),
                    @ApiResponse(responseCode = "409", description = "Update not allowed") })
    @PutMapping("/{id}")
    public TodoItem updateTodoItem(
            @Parameter(description = "ID of the TodoItem") @PathVariable Integer id,
            @RequestBody TodoItemRequest item) throws ActionNotAllowedException, ItemNotFoundException {
        return TodoItemMapper.toDto(todoItemService.updateItem(id, item));
    }

    /**
     * Mark a TodoItem as 'DONE'
     *
     * @param id  ID of the TodoItem
     * @return updated {@link TodoItem}
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Operation(summary = "Mark a TodoItem as DONE",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated the TodoItem"),
                    @ApiResponse(responseCode = "404", description = "TodoItem not found") })
    @PatchMapping("/{id}/done")
    public TodoItem markAsDone(
            @Parameter(description = "ID of the TodoItem") @PathVariable Integer id)
            throws ItemNotFoundException {
        return TodoItemMapper.toDto(todoItemService.markAsDone(id));
    }

    /**
     * Mark a TodoItem as 'NOT DONE'
     *
     * @param id  ID of the TodoItem
     * @return updated {@link TodoItem}
     * @throws ActionNotAllowedException if the item for the input id is past due
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Operation(summary = "Mark a TodoItem as NOT DONE",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated the TodoItem"),
                    @ApiResponse(responseCode = "404", description = "TodoItem not found"),
                    @ApiResponse(responseCode = "409", description = "Action not allowed") })
    @PatchMapping("/{id}/not-done")
    public TodoItem markAsNotDone(
            @Parameter(description = "ID of the TodoItem") @PathVariable Integer id)
            throws ActionNotAllowedException, ItemNotFoundException {
        return TodoItemMapper.toDto(todoItemService.markAsNotDone(id));
    }

    /**
     * Delete a TodoItem
     *
     * @param id  ID of the TodoItem
     */
    @Operation(summary = "Delete a TodoItem" )
    @DeleteMapping("/{id}")
    public void deleteTodoItem(
            @Parameter(description = "ID of the TodoItem") @PathVariable Integer id) {
        todoItemService.deleteItem(id);
    }

}
