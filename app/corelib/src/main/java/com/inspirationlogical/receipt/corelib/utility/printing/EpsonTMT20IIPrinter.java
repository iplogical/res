package com.inspirationlogical.receipt.corelib.utility.printing;

import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintException;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class EpsonTMT20IIPrinter implements Printer {

    private final static Logger logger = LoggerFactory.getLogger(EpsonTMT20IIPrinter.class);

    private static final String PRINTER_NAME = "TM-T20II";

    private static boolean jobRunning = true;

    private PrintService service;

    EpsonTMT20IIPrinter(){
        List<PrintService> service = Arrays.asList(PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE,null));
        List<PrintService> filtered = service.stream()
                .filter((s) -> s.getName().contains(PRINTER_NAME)).collect(Collectors.toList());
        if(filtered.isEmpty()) {
            throw new RuntimeException("Couldn't find "+ PRINTER_NAME + " printer");
        }
        this.service = filtered.get(0);
    }


    @Override
    public String getName() {
        return PRINTER_NAME;
    }

    @Override
    public void print(InputStream pdf) {
        try {
            logger.info("Start printing.");
            jobRunning = true;
            PrintServiceAttributeSet attributeSet = service.getAttributes();
            DocPrintJob job = service.createPrintJob();
            job.addPrintJobListener(new EpsonTMT20IIPrinter.JobCompleteMonitor());
            Doc doc = new SimpleDoc(pdf, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            job.print(doc, aset);
        }catch (PrintException e){
            logger.error("PrintException during print", e);
            throw new RuntimeException(e);
        }
    }

    private static class JobCompleteMonitor extends PrintJobAdapter {
        @Override
        public void printJobCompleted(PrintJobEvent jobEvent) {
            jobRunning = false;
            logger.info("The print job has finished.");
        }
    }
}
