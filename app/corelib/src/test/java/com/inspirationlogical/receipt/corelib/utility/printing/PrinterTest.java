package com.inspirationlogical.receipt.corelib.utility.printing;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.utility.ReceiptToXML;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.*;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class PrinterTest {

    @Rule
    public final BuildTestSchema schema = new BuildTestSchema();

    private InputStream pdf;

    @Before
    public void closeReceiptSaleOneAndGeneratePDF(){
        Receipt receipt = schema.getReceiptSaleTwo();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ReceiptFormatterEpsonTMT20II().convertToPDF(out,
                ReceiptToXML.ConvertToStream(receipt)
        );
        pdf = new ByteArrayInputStream(out.toByteArray(),0,out.size());
    }

    @Test
    public void test_epson_printer(){
        try {
            new EpsonTMT20IIPrinter().print(pdf);
        } catch(Exception e){
            // NO-OP
        }
    }

    @Test
    public void test_file_printer(){
        try {
            new FilePrinter().print(pdf);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_epson_printer_through_dependency_injections(){
        try {
            System.setProperty("ReceiptPrinterClass","FilePrinter");
            PrintService.create().print(pdf);
        } catch(Exception e){
            // NO-OP
        }
    }

}
