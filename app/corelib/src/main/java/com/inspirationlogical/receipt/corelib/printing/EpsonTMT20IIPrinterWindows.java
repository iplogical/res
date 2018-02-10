package com.inspirationlogical.receipt.corelib.printing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.InputStream;

public class EpsonTMT20IIPrinterWindows implements Printer {

    private final static Logger logger = LoggerFactory.getLogger(EpsonTMT20IIPrinter.class);

    private static final String PRINTER_NAME = "TM-T20II";

    @Override
    public void print(InputStream pdf) {
        logger.info("Start printing the pdf receipt.");
        PrintService[] services = PrinterJob.lookupPrintServices();
        try {
            PDDocument pdfDocument = PDDocument.load(new File(FilePrinter.getFilePath()));
            PrinterJob job = PrinterJob.getPrinterJob();
            for (PrintService service : services) {
                if (service.getName().contains(PRINTER_NAME)) {
                    job.setPrintService(service);
                }
            }
            job.setPageable(new PDFPageable(pdfDocument));
            PrintRequestAttributeSet pset = new HashPrintRequestAttributeSet();
            job.print(pset);
        } catch (Exception e) {
            logger.error("Exception during print.", e);
        }
    }
}