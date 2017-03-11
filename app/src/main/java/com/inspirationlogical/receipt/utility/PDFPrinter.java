package com.inspirationlogical.receipt.utility;

import javax.print.*;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class PDFPrinter {
    private  PrintService service;
    PDFPrinter(){
        List<PrintService> service = Arrays.asList(PrintServiceLookup.lookupPrintServices(null,null));
        List<PrintService> filtered = service.stream()
                .filter((s) -> s.getName().contains("Epson-TM-T20II")).collect(Collectors.toList());
        if(filtered.isEmpty()) {
            throw new RuntimeException("Couldn't find Epson-TM-T20II printer");
        }
        this.service = filtered.get(0);
    }

    void print(InputStream pdf) {
        try {
            DocPrintJob job = service.createPrintJob();
            Doc doc = new SimpleDoc(pdf, DocFlavor.INPUT_STREAM.PDF, null);
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            job.print(doc, aset);
        }catch (PrintException e){
            throw new RuntimeException(e);
        }
    }
}
