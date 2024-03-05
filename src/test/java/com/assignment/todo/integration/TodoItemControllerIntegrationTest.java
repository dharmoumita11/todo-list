package com.assignment.todo.integration;

import com.assignment.todo.controller.TodoItemController;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.CreateTodoItemRequest;
import com.assignment.todo.dto.UpdateTodoItemRequest;
import com.assignment.todo.exception.ActionNotAllowedException;
import com.assignment.todo.exception.ItemNotFoundException;
import com.assignment.todo.service.TodoItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoItemController.class)
public class TodoItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoItemService todoItemService;

    @Test
    void whenGetAllTodoItems_thenGetAllTodoItems() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("DONE")
                .build();
        given(todoItemService.getAllItems(true)).willReturn(Collections.singletonList(mockItem));

        mockMvc.perform(get("/api/v1/todos")
                        .param("includeAll", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":1, \"description\":\"Updated Todo Item\"}]"));
    }

    @Test
    void whenGetNotDoneTodoItems_thenGetNotDoneTodoItems() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.getAllItems(false)).willReturn(Collections.singletonList(mockItem));

        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":1, \"description\":\"Updated Todo Item\"}]"));
    }

    @Test
    void whenGetTodoItemId_thenGetTodoItemById() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.getItemDetails(1)).willReturn(mockItem);

        mockMvc.perform(get("/api/v1/todos/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":1, \"description\":\"Updated Todo Item\"}"));
    }

    @Test
    void whenGetTodoItemId_thenItemNotFound() throws Exception {
        given(todoItemService.getItemDetails(1)).willThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/api/v1/todos/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos/1\"}"));
    }

    @Test
    void whenPostTodoItem_thenCreateTodoItem() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .description("New Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.addItem(any(CreateTodoItemRequest.class))).willReturn(mockItem);

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"New Todo Item\",\"dueDateTime\":\"2050-03-05T21:11:02.021Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json(
                        "{\"description\":\"New Todo Item\"}"));
    }
    @Test
    void whenPostTodoItemNoDescription_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"  \",\"dueDateTime\":\"2050-03-06T21:11:02.021Z\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos\",\"message\":\"Invalid Request\",\"messages\":[\"description: 'must not be blank'\"]}"));
    }
    @Test
    void whenPostTodoItemPastDue_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"New Todo Item\",\"dueDateTime\":\"2023-03-06T21:11:02.021Z\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos\",\"message\":\"Invalid Request\",\"messages\":[\"dueDateTime: 'must be a future date'\"]}"));
    }

    @Test
    void whenPutTodoItem_thenUpdateTodoItem() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.updateItem(anyInt(), any(UpdateTodoItemRequest.class))).willReturn(mockItem);

        mockMvc.perform(put("/api/v1/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated Todo Item\",\"dueDateTime\":\"2025-03-06T21:11:02.021Z\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":1, \"description\":\"Updated Todo Item\"}"));
    }

    @Test
    void whenPutTodoItem_thenItemNotFound() throws Exception {
        given(todoItemService.updateItem(anyInt(), any(UpdateTodoItemRequest.class)))
                .willThrow(ItemNotFoundException.class);

        mockMvc.perform(put("/api/v1/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated Todo Item\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos/1\"}"));
    }

    @Test
    void whenPutTodoItem_thenActionNotAllowed() throws Exception {
        given(todoItemService.updateItem(anyInt(), any(UpdateTodoItemRequest.class)))
                .willThrow(ActionNotAllowedException.class);

        mockMvc.perform(put("/api/v1/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated Todo Item\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos/1\"}"));
    }

    @Test
    void whenPutTodoItem_thenBadRequest() throws Exception {
        mockMvc.perform(put("/api/v1/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated Todo Item\",\"dueDateTime\":\"2023-03-06T21:11:02.021Z\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos/1\",\"message\":\"Invalid Request\",\"messages\":[\"dueDateTime: 'must be a future date'\"]}"));
    }

    @Test
    void whenPatchTodoItemDone_thenUpdateTodoItemStatusToDone() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("DONE")
                .build();
        given(todoItemService.markAsDone(1)).willReturn(mockItem);

        mockMvc.perform(patch("/api/v1/todos/1/done"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":1, \"description\":\"Updated Todo Item\",\"status\":\"done\"}"));
    }

    @Test
    void whenPatchTodoItemDone_thenItemNotFound() throws Exception {
        given(todoItemService.markAsDone(1)).willThrow(ItemNotFoundException.class);

        mockMvc.perform(patch("/api/v1/todos/1/done"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos/1/done\"}"));
    }

    @Test
    void whenPatchTodoItemNotDone_thenUpdateTodoItemStatusToNotDone() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.markAsNotDone(1)).willReturn(mockItem);

        mockMvc.perform(patch("/api/v1/todos/1/not-done"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":1, \"description\":\"Updated Todo Item\",\"status\":\"not done\"}"));
    }

    @Test
    void whenPatchTodoItemNotDone_thenItemNotFound() throws Exception {
        given(todoItemService.markAsNotDone(1)).willThrow(ItemNotFoundException.class);

        mockMvc.perform(patch("/api/v1/todos/1/not-done"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos/1/not-done\"}"));
    }

    @Test
    void whenPatchTodoItemNotDone_thenActionNotAllowed() throws Exception {
        given(todoItemService.markAsNotDone(1)).willThrow(ActionNotAllowedException.class);

        mockMvc.perform(patch("/api/v1/todos/1/not-done"))
                .andExpect(status().isConflict())
                .andExpect(content().json(
                        "{\"path\":\"/api/v1/todos/1/not-done\"}"));
    }

    @Test
    void whenDeleteTodoItem_thenDeleteTodoItem() throws Exception {
        mockMvc.perform(delete("/api/v1/todos/1"))
                .andExpect(status().isOk());
        verify(todoItemService).deleteItem(1);
    }

}
