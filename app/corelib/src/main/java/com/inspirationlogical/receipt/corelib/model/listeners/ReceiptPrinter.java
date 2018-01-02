package com.inspirationlogical.receipt.corelib.model.listeners;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.inspirationlogical.receipt.corelib.jaxb.ObjectFactory;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;
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

    private final static Logger logger = LoggerFactory.getLogger(ReceiptPrinter.class);

    private static List<Printer> printers;

    static {
        printers = new ArrayList<>();
        PrintService printService = PrintService.create();
        if(printService != null) {
            printers.add(printService.getPrinter());
        }
        printers.add(new FilePrinter());
    }


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
            ByteArrayOutputStream out = null;
            InputStream xmlReceipt = null;
            try {
                logger.info("Starting the process of printing the receipt: " +  receipt.toString());
                ReceiptToXML converter = new ReceiptToXML(new ObjectFactory());
                xmlReceipt = converter.convertToXMLStream(receipt);
                out = FormatterService.create().convertToPDF(xmlReceipt);
                Wrapper<ByteArrayOutputStream> outWrapper = new Wrapper<>();
                outWrapper.setContent(out);
                printers.forEach(printer -> printPdf(printer, outWrapper));
                logger.info("Printing executed successfully");
            }
            finally {
                closeInputStream(xmlReceipt);
                closeOutputStream(out);
            }
        });
    }

    private void printPdf(Printer printer, Wrapper<ByteArrayOutputStream> outWrapper) {
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(outWrapper.getContent().toByteArray(),0, outWrapper.getContent().size());
            printer.print(in);
        } finally {
            closeInputStream(in);
        }
    }

    private void closeInputStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                logger.error("Exception while closing input stream.", e);
            }
        }
    }

    private void closeOutputStream(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                logger.error("Exception while closing output stream.", e);
            }
        }
    }
}
