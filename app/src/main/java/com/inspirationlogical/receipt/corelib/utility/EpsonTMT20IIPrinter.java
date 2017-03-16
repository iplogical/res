package com.inspirationlogical.receipt.corelib.utility;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class EpsonTMT20IIPrinter implements Printer {
    private  PrintService service;
    private static final String PRINTER_NAME = "Epson-TM-T20II";

    EpsonTMT20IIPrinter(){
        List<PrintService> service = Arrays.asList(PrintServiceLookup.lookupPrintServices(null,null));
        List<PrintService> filtered = service.stream()
                .filter((s) -> s.getName().contains(PRINTER_NAME)).collect(Collectors.toList());
        if(filtered.isEmpty()) {
            throw new RuntimeException("Couldn't find "+PRINTER_NAME+ " printer");
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
            DocPrintJob job = service.createPrintJob();
            Doc doc = new SimpleDoc(pdf, DocFlavor.INPUT_STREAM.PDF, null);
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            job.print(doc, aset);
        }catch (PrintException e){
            throw new RuntimeException(e);
        }
    }
}
