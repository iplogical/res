package com.inspirationlogical.receipt.corelib.utility.printing;
import static org.junit.Assert.*;

import java.io.*;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.inspirationlogical.receipt.corelib.jaxb.ObjectFactory;
import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;

import org.junit.Test;

public class ReceiptToXMLTest extends TestBase {

    @Test
    public void test_receipt_XML_created_from_a_closed_recipt_can_be_validated_against_the_schema() {
        try {
            Receipt receipt = schema.getReceiptSaleTwo();
            String xml_doc =  new BufferedReader(new InputStreamReader(new ReceiptToXML(new ObjectFactory()).convertToXMLStream(receipt)))
                    .lines().collect(Collectors.joining("\n"));
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            Schema schema = factory.newSchema(new StreamSource(classloader.getResourceAsStream("schema/receipt.xsd")));
            schema.newValidator().validate(new StreamSource( new ByteArrayInputStream(xml_doc.getBytes())));
            assertNotNull(xml_doc);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    @Test
    public void test_receipt_can_be_converted_to_PDF_from_XML(){
        try {
            Receipt receipt = schema.getReceiptSaleTwo();
            new ReceiptFormatterEpsonTMT20II().convertToPDF(
                    new ReceiptToXML(new ObjectFactory()).convertToXMLStream(receipt)
            );

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_receipt_formatting_through_dependency_injection(){
        Receipt receipt = schema.getReceiptSaleTwo();
        FormatterService.create().convertToPDF(new ReceiptToXML(new ObjectFactory()).convertToXMLStream(receipt));
    }

}
