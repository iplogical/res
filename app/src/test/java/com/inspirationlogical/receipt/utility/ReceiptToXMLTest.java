package com.inspirationlogical.receipt.utility;
import static org.junit.Assert.*;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.adapter.ReceiptAdapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ReceiptToXMLTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();


    @Test
    public void test_receipt_XML_created_from_a_closed_recipt_can_be_validated_against_the_schema() {
        try {
            ReceiptAdapter ra = new ReceiptAdapter(schema.getReceiptSaleOne(), manager);
            ra.close(Arrays.asList());
            String xml_doc =  new BufferedReader(new InputStreamReader(ReceiptToXML.ConvertToStream(ra)))
                    .lines().collect(Collectors.joining("\n"));
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new File("src/main/resources/schema/receipt.xsd")));
            schema.newValidator().validate(new StreamSource( new ByteArrayInputStream(xml_doc.getBytes())));
            assertNotNull(xml_doc);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    @Test
    public void test_receipt_can_be_converted_to_PDF_from_XML(){
        try {
            ReceiptAdapter ra = new ReceiptAdapter(schema.getReceiptSaleOne(), manager);
            ra.close(Arrays.asList());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new ReceiptXMLToPDFEpsonTMT20II().convertToPDF(new FileOutputStream("test.pdf"),
                    ReceiptToXML.ConvertToStream(ra)
            );

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
