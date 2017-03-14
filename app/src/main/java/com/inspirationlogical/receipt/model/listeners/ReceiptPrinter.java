package com.inspirationlogical.receipt.model.listeners;

import com.inspirationlogical.receipt.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.utility.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public class ReceiptPrinter implements ReceiptAdapter.Listener {

    @Override
    public void onOpen(ReceiptAdapter receipt) {
        // No-op
    }

    @Override
    public void onClose(ReceiptAdapter receipt) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReceiptXMLtoPDF.getDefaultInstance().convertToPDF(out,ReceiptToXML.ConvertToStream(receipt));
        Printer.getDefaultInstance().print(new ByteArrayInputStream(out.toByteArray(),0,out.size()));
    }
}
