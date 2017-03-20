package com.inspirationlogical.receipt.corelib.exception;

/**
 * Created by Bálint on 2017.03.20..
 */
public class IllegalReceiptStateException  extends RuntimeException {

    public IllegalReceiptStateException(String message) {
        super("This operation is illegal in this state. " + message);
    }

}
