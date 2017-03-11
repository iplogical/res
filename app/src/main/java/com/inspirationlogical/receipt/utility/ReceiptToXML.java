package com.inspirationlogical.receipt.utility;

import com.inspirationlogical.receipt.jaxb.*;
import com.inspirationlogical.receipt.model.Restaurant;
import com.inspirationlogical.receipt.model.adapter.ReceiptAdapter;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class ReceiptToXML {

    public static Receipt Convert(ReceiptAdapter receiptAdapter){
        return createReceipt(receiptAdapter, new ObjectFactory());
    }

    public static InputStream ConvertToStream(ReceiptAdapter receiptAdapter){
        Receipt r = createReceipt(receiptAdapter, new ObjectFactory());
        try {
            JAXBContext context = JAXBContext.newInstance("com.inspirationlogical.receipt.jaxb");
            Marshaller jaxbMarshaller = context.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            jaxbMarshaller.marshal(r, baos);
            return new ByteArrayInputStream(baos.toByteArray(),0,baos.size());
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    private static Receipt createReceipt(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        Receipt receipt = factory.createReceipt();
        receipt.setHeader(createHeader(receiptAdapter, factory));
        receipt.setBody(createReceiptBody(receiptAdapter, factory));
        receipt.setFooter(createFooter(receiptAdapter, factory));
        return receipt;
    }

    private static ReceiptFooter createFooter(ReceiptAdapter receiptAdapter,ObjectFactory factory) {
        ReceiptFooter footer = factory.createReceiptFooter();
        //TODO: add disclaimer in restaurant???
        footer.setDisclaimer(Resources.PRINTER.getString("Disclaimer"));
        footer.setGreet(Resources.PRINTER.getString("Greet"));
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(receiptAdapter.getAdaptee().getClosureTime().getTimeInMillis());
        footer.setDatetime(new XMLGregorianCalendarImpl(gc));
        footer.setReceiptIdTag(Resources.PRINTER.getString("ReceipIDTag"));
        footer.setReceiptId(receiptAdapter.getAdaptee().getId().toString());
        return footer;
    }

    private static ReceiptHeader createHeader(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptHeader header = factory.createReceiptHeader();
        Restaurant restaurant = receiptAdapter.getAdaptee().getOwner().getOwner();
        header.setCompanyName(restaurant.getCompanyName());
        //FIXME: add owner and restaurant ZIP,Street addr,city, taxation ID in DataModel
        header.setCompanyLocZIP("1111");
        header.setCompanyLocCity(restaurant.getAddress());
        header.setCompanyLocStreet("Lonyai 2.");
        header.setCompanyTaxpayerId("1234567-8-90");
        header.setRestaurantName(restaurant.getCompanyName());
        header.setRestaurantLocZIP("1111");
        header.setRestaurantLocCity(restaurant.getAddress());
        header.setRestaurantLocStreet("Lonyai 2.");
        return  header;
    }

    private static ReceiptBody createReceiptBody(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBody body = factory.createReceiptBody();
        body.setBodyHeader(createReceiptBodyHeader(receiptAdapter,factory));
        List<ReceiptBodyEntry> records = receiptAdapter.getAdaptee().getRecords().stream().map((record) ->{
            ReceiptBodyEntry entry = factory.createReceiptBodyEntry();
            entry.setName(record.getName());
            entry.setQtyPrice(BigInteger.valueOf(record.getSalePrice()));
            entry.setQty(BigInteger.valueOf((int)record.getQuantity()));
            entry.setQtyDim(record.getProduct().getEtalonQuantity().toString());
            entry.setTotal(BigInteger.valueOf((int)record.getQuantity()*record.getSalePrice()));
            return entry;
        }).collect(Collectors.toList());
        body.getEntry().addAll(records);
        body.setBodyFooter(createReceiptBodyFooter(receiptAdapter,factory));
        return body;
    }

    private static ReceiptBodyHeader createReceiptBodyHeader(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBodyHeader header = factory.createReceiptBodyHeader();
        header.setNameHeader(Resources.PRINTER.getString("NameHeader"));
        header.setQtyDimHeader(Resources.PRINTER.getString("QtyDimHeader"));
        header.setQtyHeader(Resources.PRINTER.getString("QtyHeader"));
        header.setQtyPriceHeader(Resources.PRINTER.getString("QtyPriceHeader"));
        header.setTotalHeader(Resources.PRINTER.getString("TotalHeader"));
        return header;
    }

    private static ReceiptBodyFooter createReceiptBodyFooter(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBodyFooter footer = factory.createReceiptBodyFooter();
        footer.setTotalTag(Resources.PRINTER.getString("TotalTag"));
        footer.setTotal(BigInteger.valueOf(
                receiptAdapter.getAdaptee().getRecords().stream()
                        .map(e -> e.getSalePrice()*e.getQuantity())
                        .reduce(0.0,(x,y)-> x+y).intValue())
        );
        //FIXME: add currency in model.Receipt
        footer.setTotalCurrency(Resources.PRINTER.getString("TotalCurrency"));
        footer.setTotalRoundedTag(Resources.PRINTER.getString("TotalRoundedTag"));
        footer.setTotalRoundedCurrency(footer.getTotalCurrency());
        footer.setPaymentMethodTag(Resources.PRINTER.getString("PaymentMethod"));
        footer.setPaymentMethod(receiptAdapter.getAdaptee().getPaymentMethod().toString());
        //TODO: add rounding logic based on paymentmethod
        footer.setTotalRounded(footer.getTotal());
        return footer;
    }
}
