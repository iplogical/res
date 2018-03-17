package com.inspirationlogical.receipt.corelib.printing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
@Component
public class FormatterService {

    @Autowired
    private static ReceiptFormatter formatter;

    public ByteArrayOutputStream convertToPDF(InputStream receipt){
        return formatter.convertToPDF(receipt);
    }

    public static FormatterService create(){
        // TODO_FIX_PRINT
//        return formatter.;
        return null;
    }
}
