package com.inspirationlogical.receipt.corelib.printing;

import com.inspirationlogical.receipt.corelib.params.ReceiptPrintModel;
import com.inspirationlogical.receipt.corelib.params.ReceiptRecordPrintModel;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PrinterJob;
import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.now;

@RunWith(SpringRunner.class)
public class ReceiptPdfCreatorTest {

    @TestConfiguration
    static class WeighingTicketCreatorTestConfiguration {

        @Bean
        public ReceiptPdfCreator weighingTicketCreator() {
            return new ReceiptPdfCreator();
        }
    }

    @Autowired
    private ReceiptPdfCreator receiptPdfCreator;

    private ReceiptPrintModel receiptPrintModel;

    @Before
    public void initReceiptPrintModel() {
        receiptPrintModel = ReceiptPrintModel.builder()
                .restaurantName("GameUp Pub")
                .restaurantAddress("1066 Budapest, Zichy Jenő utca 4.")
                .restaurantPhoneNumber("+36 30/287-87-66")
                .restaurantSocialMediaInfo("facebook.com/gameuppub")
                .restaurantWebsite("www.gameup.hu")
                .receiptRecordsPrintModels(new ArrayList<>())
                .totalPriceNoServiceFee(2680)
                .totalDiscount(0) // TODO
                .discountedTotalPrice(2680)
                .roundedTotalPrice(2680)
                .paymentMethod("Készpénz")
                .receiptDisclaimer("Nem adóügyi bizonylat.\nKészpénz átvételére nem jogosít.")
                .receiptNote("Szerdán akció!")
                .receiptGreet("Köszönjük, hogy a vendégünk volt!")
                .closureTime(now())
                .receiptId(500)
                .build();

        ReceiptRecordPrintModel productModel1 = ReceiptRecordPrintModel.builder()
                .productName("Vodka")
                .discount(false)
                .soldQuantity(2)
                .productPrice(580)
                .totalPrice(1360)
                .build();
        ReceiptRecordPrintModel productModel2 = ReceiptRecordPrintModel.builder()
                .productName("Gin Tonic")
                .discount(true)
                .soldQuantity(3)
                .productPrice(440)
                .totalPrice(1320)
                .build();
        receiptPrintModel.getReceiptRecordsPrintModels().add(productModel1);
        receiptPrintModel.getReceiptRecordsPrintModels().add(productModel2);
    }

    @Test
    public void createReceiptPdf() throws Exception {
        byte[] receiptPdf = receiptPdfCreator.createReceiptPdf(receiptPrintModel);
        File receiptPdfFile = new File("temp/receipt.pdf");
        FileUtils.writeByteArrayToFile(receiptPdfFile, receiptPdf);
   //     printPdf(receiptPdfFile);
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
        PrintService printService = findPrintService();
        printPdf(pdf, printService);
    }

    private PrintService findPrintService() {
        List<PrintService> printServices = Arrays.asList(PrintServiceLookup.lookupPrintServices(null, null));
        return printServices.stream()
                .filter(service -> service.getName().contains("TM-T20II"))
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

        }
    }

}