package com.assignment.todo.constants;

public enum TodoItemStatus {

    NOT_DONE ("not done"),
    DONE ("done"),
    PAST_DUE ("past due");

    private final String value;

    TodoItemStatus(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public TodoItemStatus from(String input) {
        for (TodoItemStatus status : values()) {
            if (status.value.equalsIgnoreCase(input)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + input);
    }

}
