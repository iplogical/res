package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.inject.Inject;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
public class FormatterService {
    @Inject
    private ReceiptFormatter formatter;

    public ByteArrayOutputStream convertToPDF(InputStream receipt){
        return formatter.convertToPDF(receipt);
    }

    public static FormatterService create(){
        return PrintingInjector.getInjector().getInstance(FormatterService.class);
    }
}
