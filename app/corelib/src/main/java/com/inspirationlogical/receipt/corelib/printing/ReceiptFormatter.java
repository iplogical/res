package com.inspirationlogical.receipt.corelib.printing;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public interface ReceiptFormatter {
    ByteArrayOutputStream convertToPDF(InputStream receipt);
}
