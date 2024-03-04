package com.assignment.todo.service.impl;

import com.assignment.todo.dal.dao.TodoItemEntityRepository;
import com.assignment.todo.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoItemServiceImpl implements TodoItemService {

    private final TodoItemEntityRepository todoItemEntityRepository;

    @Autowired
    public TodoItemServiceImpl(TodoItemEntityRepository todoItemEntityRepository) {
        this.todoItemEntityRepository = todoItemEntityRepository;
    }
}
