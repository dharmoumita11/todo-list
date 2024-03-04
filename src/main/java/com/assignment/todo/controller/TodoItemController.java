package com.assignment.todo.controller;

import com.assignment.todo.dto.TodoItem;
import com.assignment.todo.dto.TodoItemRequest;
import com.assignment.todo.service.TodoItemService;
import com.assignment.todo.util.TodoItemMapper;
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

    @GetMapping
    public List<TodoItem> getAllNotDoneItems(@RequestParam(required = false, defaultValue = "false") boolean includeAll) {
        return todoItemService.getAllItems(includeAll).stream()
                .map(TodoItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TodoItem getTodoItemDetails(@PathVariable Integer id) {
        return TodoItemMapper.toDto(todoItemService.getItemDetails(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoItem addTodoItem(@RequestBody TodoItemRequest item) {
        return TodoItemMapper.toDto(todoItemService.addItem(item));
    }

    @PutMapping("/{id}")
    public TodoItem updateTodoItem(@PathVariable Integer id, @RequestBody TodoItemRequest item) {
        return TodoItemMapper.toDto(todoItemService.updateItem(id, item));
    }

    @PatchMapping("/{id}/done")
    public TodoItem markAsDone(@PathVariable Integer id) {
        return TodoItemMapper.toDto(todoItemService.markAsDone(id));
    }

    @PatchMapping("/{id}/not-done")
    public TodoItem markAsNotDone(@PathVariable Integer id) {
        return TodoItemMapper.toDto(todoItemService.markAsNotDone(id));
    }

    @DeleteMapping("/{id}")
    public void deleteTodoItem(@PathVariable Integer id) {
        todoItemService.deleteItem(id);
    }

}
