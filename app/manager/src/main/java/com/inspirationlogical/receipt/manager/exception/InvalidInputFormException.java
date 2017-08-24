package com.inspirationlogical.receipt.manager.exception;

public class InvalidInputFormException extends Exception {

    public InvalidInputFormException(String message) {
        super("Invalid input for the ProductForm: " + message);
    }
}
