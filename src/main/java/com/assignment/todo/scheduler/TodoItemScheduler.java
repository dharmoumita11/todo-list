package com.assignment.todo.scheduler;

import com.assignment.todo.service.TodoItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TodoItemScheduler {

    private final TodoItemService todoItemService;

    @Autowired
    public TodoItemScheduler(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * Scheduled job to run every minute
     * <p>Checks all TodoItems that are not in 'DONE' state,
     * and updates their state to 'PAST DUE' if it's past their due date.</p>
     */
    //@Scheduled(cron = "0 * * * * *") // Runs every minute, adjust as necessary
    @Scheduled(fixedDelay = 60000) // Every minute
    public void updatePastDueItemsStatus() {
        log.info("Scheduled job to update PAST_DUE items starting");
        todoItemService.checkAndUpdateStatusForPastDueItems();
    }

}
