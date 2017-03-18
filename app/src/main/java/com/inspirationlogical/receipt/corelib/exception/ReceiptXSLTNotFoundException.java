package com.inspirationlogical.receipt.corelib.exception;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
public class ReceiptXSLTNotFoundException extends RuntimeException {
    public ReceiptXSLTNotFoundException(String message) {
        super("Couldn't find class xslt file for receipt formatting:"+ message);
    }
}
