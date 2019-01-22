package com.inspirationlogical.receipt.corelib.printing;

import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.params.ReceiptPrintModel;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PrinterJob;
import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.now;

@Component
public class ReceiptPrinterImpl implements ReceiptPrinter {

    private final static Logger logger = LoggerFactory.getLogger(ReceiptPrinterImpl.class);

    private static final String PRINTER_NAME = "TM-T20II";

    @Autowired
    private ReceiptPdfCreator receiptPdfCreator;

    @Override
    public void printReceipt(ReceiptPrintModel receiptPrintModel) {
        BackgroundThread.execute(() -> {
            logger.info("Start printing of the the receipt: " + receiptPrintModel);
            byte[] receiptPdf = receiptPdfCreator.createReceiptPdf(receiptPrintModel);
            printPdf(receiptPdf);
            logger.info("Printing executed successfully");
        });
    }

    private void printPdf(byte[] receiptPdf) {
        File pdf = saveToFile(receiptPdf);
        printPdf(pdf);
    }

    private File saveToFile(byte[] receiptPdfBytes) {
        File receiptPdf;
        try {
            receiptPdf = new File(getFilePath());
            FileUtils.writeByteArrayToFile(receiptPdf, receiptPdfBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return receiptPdf;
    }

    private String getFilePath() {
        String fileName = "receipt_" + now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")) + ".pdf";
        String path = Resources.CONFIG.getString("ReceiptOutDir") + File.separator + fileName;
        return Paths.get(path).toString();
    }

    private void printPdf(File pdf) {
        logger.info("Start printing the pdf receipt.");
        PrintService printService = findPrintService();
        printPdf(pdf, printService);
    }

    private PrintService findPrintService() {
        List<PrintService> printServices = Arrays.asList(PrintServiceLookup.lookupPrintServices(null, null));
        return printServices.stream()
                .filter(service -> service.getName().contains(PRINTER_NAME))
                .findFirst().orElse(null);
    }

    private void printPdf(File pdf, PrintService printService) {
        try {
            PDDocument pdfDocument = PDDocument.load(pdf);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printService);
            job.setPageable(new PDFPageable(pdfDocument));
            job.print(new HashPrintRequestAttributeSet());
        } catch (Exception e) {
            logger.error("Exception during print.", e);
        }
    }
}
