package com.assignment.todo.exception;

public class ItemNotFoundException extends Exception {

    public ItemNotFoundException(final Integer id) {
        super("Item id " + id + " not found");
    }

}
