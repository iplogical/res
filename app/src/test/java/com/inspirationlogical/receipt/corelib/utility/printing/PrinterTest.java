package com.inspirationlogical.receipt.corelib.utility.printing;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.utility.ReceiptToXML;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class PrinterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    private InputStream pdf;

    @Before
    public void closeReceiptSaleOneAndGeneratePDF(){
        ReceiptAdapter ra = new ReceiptAdapter(schema.getReceiptSaleOne(), schema.getEntityManager());
        ra.close(Arrays.asList());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ReceiptFormatterEpsonTMT20II().convertToPDF(out,
                ReceiptToXML.ConvertToStream(ra)
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
