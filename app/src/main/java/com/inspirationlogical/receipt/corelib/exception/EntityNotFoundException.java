package com.inspirationlogical.receipt.corelib.exception;

public abstract class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message + " entry could not be retrieved from database!");
    }
}
