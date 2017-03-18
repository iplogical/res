package com.inspirationlogical.receipt.corelib.exception;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
public class PrinterClassNotFoundException extends RuntimeException {
    public PrinterClassNotFoundException(String message) {
        super("Couldn't find class for printing with name:"+ message);
    }
}
