package com.inspirationlogical.receipt.corelib.utility;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public interface ReceiptXMLtoPDF {
    static ReceiptXMLtoPDF getDefaultInstance(){
        return new ReceiptXMLToPDFEpsonTMT20II();
    }
    void convertToPDF(OutputStream out, InputStream receipt);
}
