package com.inspirationlogical.receipt.utility;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.inspirationlogical.receipt.jaxb.Receipt;
import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.adapter.ReceiptAdapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import sun.misc.IOUtils;

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
            Receipt r = ReceiptToXML.Convert(ra);
            JAXBContext context = JAXBContext.newInstance("com.inspirationlogical.receipt.jaxb");
            Marshaller jaxbMarshaller = context.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            jaxbMarshaller.marshal(r, baos );
            String xml_doc =  baos.toString();
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new File("src/main/resources/schema/receipt.xsd ")));
            schema.newValidator().validate(new StreamSource( new ByteArrayInputStream(xml_doc.getBytes())));
            assertNotNull(r);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    @Before
    public void persistReceipt() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getReceiptSaleOne());
        manager.getTransaction().commit();
        manager.detach(schema.getReceiptSaleOne());
    }
}
