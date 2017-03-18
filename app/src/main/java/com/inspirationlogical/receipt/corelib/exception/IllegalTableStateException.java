package com.inspirationlogical.receipt.corelib.exception;

/**
 * Created by Bálint on 2017.03.15..
 */
public class IllegalTableStateException extends RuntimeException {

    public IllegalTableStateException(String message) {
        super("This operation is illegal in this state. " + message);
    }
}
