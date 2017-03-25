package com.inspirationlogical.receipt.corelib.exception;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
public class FormatterClassNotFound extends RuntimeException {
    public FormatterClassNotFound(String message) {
        super("Couldn't find class for Receipt XML formatter with name:" + message);
    }
}
