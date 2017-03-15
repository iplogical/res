package com.inspirationlogical.receipt.exception;

public class TableNotFoundException extends EntityNotFoundException {

    public TableNotFoundException() {
        super("Table");
    }
}
