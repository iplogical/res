package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.utility.*;
import com.inspirationlogical.receipt.corelib.utility.printing.FilePrinter;
import com.inspirationlogical.receipt.corelib.utility.printing.FormatterService;
import com.inspirationlogical.receipt.corelib.utility.printing.PrintService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public class ReceiptPrinter implements ReceiptAdapter.Listener {

    @Override
    public void onOpen(ReceiptAdapter receipt) {
        // No-op
    }

    /**
     * by default prints through printer and also through File Printer
     * @param receipt to print
     */
    @Override
    public void onClose(ReceiptAdapter receipt) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FormatterService.create().convertToPDF(out,ReceiptToXML.ConvertToStream(receipt));
        PrintService.create().print(new ByteArrayInputStream(out.toByteArray(),0,out.size()));
        new FilePrinter().print(new ByteArrayInputStream(out.toByteArray(),0,out.size()));
    }
}
