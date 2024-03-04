package com.assignment.todo.integration;

import com.assignment.todo.controller.TodoItemController;
import com.assignment.todo.dal.entity.TodoItemEntity;
import com.assignment.todo.dto.TodoItemRequest;
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
    public void whenGetAllTodoItems_thenGetAllTodoItems() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("DONE")
                .build();
        given(todoItemService.getAllItems(true)).willReturn(Collections.singletonList(mockItem));

        mockMvc.perform(get("/api/v1/todos")
                        .param("includeAll", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1, \"description\":\"Updated Todo Item\"}]"));
    }

    @Test
    public void whenGetNotDoneTodoItems_thenGetNotDoneTodoItems() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.getAllItems(false)).willReturn(Collections.singletonList(mockItem));

        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1, \"description\":\"Updated Todo Item\"}]"));
    }

    @Test
    public void whenGetTodoItemId_thenGetTodoItemById() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.getItemDetails(1)).willReturn(mockItem);

        mockMvc.perform(get("/api/v1/todos/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"description\":\"Updated Todo Item\"}"));
    }

    @Test
    public void whenPostTodoItem_thenCreateTodoItem() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .description("New Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.addItem(any(TodoItemRequest.class))).willReturn(mockItem);

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"New Todo Item\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"description\":\"New Todo Item\"}"));
    }

    @Test
    public void whenPutTodoItem_thenUpdateTodoItem() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.updateItem(anyInt(), any(TodoItemRequest.class))).willReturn(mockItem);

        mockMvc.perform(put("/api/v1/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated Todo Item\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"description\":\"Updated Todo Item\"}"));
    }

    @Test
    public void whenPatchTodoItemDone_thenUpdateTodoItemStatusToDone() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("DONE")
                .build();
        given(todoItemService.markAsDone(1)).willReturn(mockItem);

        mockMvc.perform(patch("/api/v1/todos/1/done"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"description\":\"Updated Todo Item\",\"status\":\"done\"}"));
    }

    @Test
    public void whenPatchTodoItemNotDone_thenUpdateTodoItemStatusToNotDone() throws Exception {
        TodoItemEntity mockItem = TodoItemEntity.builder()
                .id(1)
                .description("Updated Todo Item")
                .status("NOT_DONE")
                .build();
        given(todoItemService.markAsNotDone(1)).willReturn(mockItem);

        mockMvc.perform(patch("/api/v1/todos/1/not-done"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"description\":\"Updated Todo Item\",\"status\":\"not done\"}"));
    }

    @Test
    public void whenDeleteTodoItem_thenDeleteTodoItem() throws Exception {
        mockMvc.perform(delete("/api/v1/todos/1"))
                .andExpect(status().isOk());
        verify(todoItemService).deleteItem(1);
    }

}