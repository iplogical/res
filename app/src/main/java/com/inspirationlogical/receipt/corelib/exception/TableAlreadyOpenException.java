package com.inspirationlogical.receipt.corelib.exception;

/**
 * Created by Bálint on 2017.03.15..
 */
public class TableAlreadyOpenException extends RuntimeException {

    public TableAlreadyOpenException(int message) {
        super("The table is already open. Table number: " + message);
    }
}
