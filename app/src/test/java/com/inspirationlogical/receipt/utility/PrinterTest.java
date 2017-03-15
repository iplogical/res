package com.inspirationlogical.receipt.utility;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.adapter.ReceiptAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.io.*;
import java.util.Arrays;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class PrinterTest {
    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void test_epson_printer(){
        try {
            ReceiptAdapter ra = new ReceiptAdapter(schema.getReceiptSaleOne(), manager);
            ra.close(Arrays.asList());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ReceiptXMLToPDFEpsonTMT20II().convertToPDF(out,
                    ReceiptToXML.ConvertToStream(ra)
            );
            InputStream pdf_in = new ByteArrayInputStream(out.toByteArray(),0,out.size());
            new EpsonTMT20IIPrinter().print(pdf_in);
        } catch(Exception e){
            // NO-OP
        }
    }

    @Test
    public void test_file_printer(){
        try {
            ReceiptAdapter ra = new ReceiptAdapter(schema.getReceiptSaleOne(), manager);
            ra.close(Arrays.asList());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ReceiptXMLToPDFEpsonTMT20II().convertToPDF(out,ReceiptToXML.ConvertToStream(ra));
            InputStream pdf_in = new ByteArrayInputStream(out.toByteArray(),0,out.size());
            new FilePrinter().print(pdf_in);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Before
    public void persistReceipt() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getReceiptSaleOne());
        manager.getTransaction().commit();
    }
}
