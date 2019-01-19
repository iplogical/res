package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.jaxb.ObjectFactory;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.printing.*;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptServicePay;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReceiptPrinter implements ReceiptServicePay.Listener {

    private final static Logger logger = LoggerFactory.getLogger(ReceiptPrinter.class);

    private static List<Printer> printers;

    @Autowired
    private ApplicationContext applicationContext;

    static {
        printers = new ArrayList<>();
        printers.add(new FilePrinter());
        PrintService printService = PrintService.create();
        if(printService != null) {
            printers.add(printService.getPrinter());
        }
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
                ReceiptToXML converter = applicationContext.getBean(ReceiptToXML.class, new ObjectFactory());
                xmlReceipt = converter.convertToXMLStream(receipt.getId());
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
