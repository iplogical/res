package com.inspirationlogical.receipt.manager.exception;

public class InvalidProductFormException extends Exception {

    public InvalidProductFormException(String message) {
        super("Invalid input for the ProductForm: " + message);
    }
}
