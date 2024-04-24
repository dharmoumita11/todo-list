package com.assignment.todo.controller;

import com.assignment.todo.dto.CreateTodoItemRequest;
import com.assignment.todo.dto.ErrorResponse;
import com.assignment.todo.dto.TodoItem;
import com.assignment.todo.dto.UpdateTodoItemRequest;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import com.assignment.todo.service.TodoItemService;
import com.assignment.todo.util.TodoItemMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/todos")
@Validated
public class TodoItemController {

    private final TodoItemService todoItemService;

    @Autowired
    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * Get all the pending TodoItems with an option to retrieve all items
     *
     * @param includeAll  fetch all the TodoItems if set to true,
     *                    otherwise fetch the TodoItems which are not done by default
     *
     * @return List of {@link TodoItem}
     */
    @Operation(summary = "Get all the pending TodoItems with an option to retrieve all items")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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
        responses = {@ApiResponse(responseCode = "200", description = "Found the TodoItem"),
            @ApiResponse(responseCode = "404", description = "TodoItem not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TodoItem getTodoItemDetails(
            @Parameter(description = "ID of the TodoItem") @PathVariable @Min(1) Integer id)
            throws ItemNotFoundException {
        return TodoItemMapper.toDto(todoItemService.getItemDetails(id));
    }

    /**
     * Add a new TodoItem to the list
     *
     * @param item  {@link CreateTodoItemRequest}
     * @return newly added {@link TodoItem}
     */
    @Operation(summary = "Add a new TodoItem to the list")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TodoItem addTodoItem(
            @Valid @RequestBody CreateTodoItemRequest item) {
        return TodoItemMapper.toDto(todoItemService.addItem(item));
    }

    /**
     * Update a TodoItem in the list
     *
     * @param item  {@link UpdateTodoItemRequest}
     * @return updated {@link TodoItem}
     * @throws ActionNotAllowedException if the item for the input id is past due
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Operation(summary = "Update a TodoItem in the list",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated the TodoItem"),
                    @ApiResponse(responseCode = "404", description = "TodoItem not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Update not allowed",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TodoItem updateTodoItem(
            @Parameter(description = "ID of the TodoItem") @PathVariable @Min(1) Integer id,
            @Valid @RequestBody UpdateTodoItemRequest item) throws ActionNotAllowedException, ItemNotFoundException {
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
                    @ApiResponse(responseCode = "404", description = "TodoItem not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
    @PatchMapping(value = "/{id}/done", produces = MediaType.APPLICATION_JSON_VALUE)
    public TodoItem markAsDone(
            @Parameter(description = "ID of the TodoItem") @PathVariable @Min(1) Integer id)
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
                    @ApiResponse(responseCode = "404", description = "TodoItem not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Action not allowed",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
    @PatchMapping(value = "/{id}/not-done", produces = MediaType.APPLICATION_JSON_VALUE)
    public TodoItem markAsNotDone(
            @Parameter(description = "ID of the TodoItem") @PathVariable @Min(1) Integer id)
            throws ActionNotAllowedException, ItemNotFoundException {
        return TodoItemMapper.toDto(todoItemService.markAsNotDone(id));
    }

    /**
     * Delete a TodoItem
     *
     * @param id  ID of the TodoItem
     * @throws ItemNotFoundException if an item for the input id doesn't exist
     */
    @Operation(summary = "Delete a TodoItem",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deleted the TodoItem"),
                    @ApiResponse(responseCode = "404", description = "TodoItem not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class))) })
    @DeleteMapping("/{id}")
    public void deleteTodoItem(
            @Parameter(description = "ID of the TodoItem") @PathVariable @Min(1) Integer id) throws ItemNotFoundException {
        todoItemService.deleteItem(id);
    }

}
