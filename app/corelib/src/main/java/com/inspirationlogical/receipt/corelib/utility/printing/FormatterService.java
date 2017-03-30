package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.inject.Inject;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
public class FormatterService {
    @Inject
    private ReceiptFormatter formatter;
    public void convertToPDF(OutputStream out, InputStream receipt){
        formatter.convertToPDF(out,receipt);
    }

    public static FormatterService create(){
        return PrintingInjector.getInjector().getInstance(FormatterService.class);
    }
}
