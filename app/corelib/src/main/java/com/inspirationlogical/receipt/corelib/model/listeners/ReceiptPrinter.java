package com.inspirationlogical.receipt.corelib.model.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.utility.ReceiptToXML;
import com.inspirationlogical.receipt.corelib.utility.printing.FilePrinter;
import com.inspirationlogical.receipt.corelib.utility.printing.FormatterService;
import com.inspirationlogical.receipt.corelib.utility.printing.PrintService;
import com.inspirationlogical.receipt.corelib.utility.printing.Printer;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public class ReceiptPrinter implements ReceiptAdapter.Listener {

    private static List<Printer> printers = Arrays.asList(
            PrintService.create().getPrinter(), // if printer not found this will become a NULL printer
            new FilePrinter()
    );

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
        BackgroundThread.execute(() -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FormatterService.create().convertToPDF(out,ReceiptToXML.ConvertToStream(receipt));
            printers.forEach(printer -> printer.print(new ByteArrayInputStream(out.toByteArray(),0,out.size())));
            System.out.println(Thread.currentThread().getName() + ": Printing executed successfully");
        });
    }
}
