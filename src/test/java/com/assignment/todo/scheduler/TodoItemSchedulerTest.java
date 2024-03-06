package com.assignment.todo.scheduler;

import com.assignment.todo.BaseTestClass;
import com.assignment.todo.service.TodoItemService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TodoItemSchedulerTest extends BaseTestClass {

    @Mock
    private TodoItemService todoItemService;

    @InjectMocks
    private TodoItemScheduler todoItemScheduler;

    @Test
    void whenUpdatePastDueItemsStatus_thenSuccess() {
        todoItemScheduler.updatePastDueItemsStatus();

        verify(todoItemService, times(1)).checkAndUpdateStatusForPastDueItems();
    }

}
