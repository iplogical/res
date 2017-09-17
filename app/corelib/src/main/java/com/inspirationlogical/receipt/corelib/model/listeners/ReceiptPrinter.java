package com.inspirationlogical.receipt.corelib.model.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import com.inspirationlogical.receipt.corelib.jaxb.ObjectFactory;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.utility.printing.ReceiptToXML;
import com.inspirationlogical.receipt.corelib.utility.printing.FilePrinter;
import com.inspirationlogical.receipt.corelib.utility.printing.FormatterService;
import com.inspirationlogical.receipt.corelib.utility.printing.PrintService;
import com.inspirationlogical.receipt.corelib.utility.printing.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public class ReceiptPrinter implements ReceiptAdapterPay.Listener {

    final static Logger logger = LoggerFactory.getLogger(ReceiptPrinter.class);

    private static List<Printer> printers = Arrays.asList(
            PrintService.create().getPrinter(), // if printer not found this will become a NULL printer
            new FilePrinter()
    );

    @Override
    public void onOpen(ReceiptAdapterPay receipt) {
        // No-op
    }

    /**
     * by default prints through printer and also through File Printer
     * @param receipt to print
     */
    @Override
    public void onClose(Receipt receipt) {
        BackgroundThread.execute(() -> {
            logger.info("Starting the process of printing the receipt: " +  receipt.toString());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ReceiptToXML converter = new ReceiptToXML(new ObjectFactory());

            FormatterService.create().convertToPDF(out, converter.convertToStream(receipt));
            printers.forEach(printer -> printer.print(new ByteArrayInputStream(out.toByteArray(),0,out.size())));
            logger.info("Printing executed successfully");
            System.out.println(Thread.currentThread().getName() + ": Printing executed successfully");
        });
    }
}
