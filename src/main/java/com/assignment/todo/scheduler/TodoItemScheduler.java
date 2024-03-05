package com.assignment.todo.scheduler;

import com.assignment.todo.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TodoItemScheduler {

    private final TodoItemService todoItemService;

    @Autowired
    public TodoItemScheduler(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    //@Scheduled(cron = "0 * * * * *") // Runs every minute, adjust as necessary
    @Scheduled(fixedDelay = 60000) // Every minute
    public void updatePastDueItemsStatus() {
        todoItemService.checkAndUpdateStatusForPastDueItems();
    }

}
