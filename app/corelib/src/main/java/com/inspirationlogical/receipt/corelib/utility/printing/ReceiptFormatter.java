package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public interface ReceiptFormatter {
    void convertToPDF(OutputStream out, InputStream receipt);
}
