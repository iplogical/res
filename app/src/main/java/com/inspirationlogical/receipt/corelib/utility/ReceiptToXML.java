package com.inspirationlogical.receipt.corelib.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.inspirationlogical.receipt.corelib.jaxb.*;
import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;


/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class ReceiptToXML {
    // TODO: Change database encoding to UTF-8. Use UTF-8 everywhere.
    public static Receipt Convert(ReceiptAdapter receiptAdapter){
        return createReceipt(receiptAdapter, new ObjectFactory());
    }

    public static InputStream ConvertToStream(ReceiptAdapter receiptAdapter){
        Receipt r = createReceipt(receiptAdapter, new ObjectFactory());
        try {
            JAXBContext context = JAXBContext.newInstance("com.inspirationlogical.receipt.corelib.jaxb");
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
        footer.setDisclaimer(Resources.PRINTER.getStringISO88591("Disclaimer"));
        //TODO: get active note from Entity and set only if present
        footer.setNote("Árvíztűrő tükörfúrógép!");
        footer.setGreet(Resources.PRINTER.getStringISO88591("Greet"));
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(receiptAdapter.getAdaptee().getClosureTime().getTimeInMillis());
        footer.setDatetime(new XMLGregorianCalendarImpl(gc));
        footer.setReceiptIdTag(Resources.PRINTER.getStringISO88591("ReceipIDTag")+":"+receiptAdapter.getAdaptee().getId().toString());
        //TODO: get vendor info from resource file, version from git tag
        footer.setVendorInfo("© InspirationLogical, Receipt v4.3");
        return footer;
    }

    private static ReceiptHeader createHeader(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptHeader header = factory.createReceiptHeader();
        Restaurant restaurant = receiptAdapter.getAdaptee().getOwner().getOwner();
        //FIXME: add owner and restaurant ZIP,Street addr,city, taxation ID in DataModel
        header.setRestaurantLogoPath(Paths.get(Resources.CONFIG.getString("ReceiptLogoPath")).toUri().toString());
        header.setRestaurantName(restaurant.getRestaurantName());
        header.setRestaurantAddress(
                String.join(",", Arrays.asList(restaurant.getRestaurantAddress().getZIPCode(),
                        restaurant.getRestaurantAddress().getCity(),
                        restaurant.getRestaurantAddress().getStreet())));
        //FIXME: add these to datamodel
        header.setRestaurantSocialMediaInfo("facebook.com/gameuppub");
        header.setRestaurantWebsite("www.gameup.hu");
        return  header;
    }

    private static void setCustomerInfo(ReceiptAdapter adapter,ReceiptBody body,ObjectFactory factory)
    {
        Client client = adapter.getAdaptee().getClient();
        if(client != null) {
            CustomerInfo customer = factory.createCustomerInfo();
            customer.setName(createTagValue(factory,Resources.PRINTER.getStringISO88591("CustomerName") ,client.getName()));
            if(!client.getAddress().isEmpty())
                customer.setAddress(createTagValue(factory,Resources.PRINTER.getStringISO88591("CustomerAddress") ,client.getAddress()));
            if(!client.getTAXNumber().isEmpty())
                customer.setTaxNumber(createTagValue(factory,Resources.PRINTER.getStringISO88591("CustomerTAXnumber") ,client.getTAXNumber()));
            body.setCustomer(customer);
        }
    }

    private static ReceiptBody createReceiptBody(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBody body = factory.createReceiptBody();
        setCustomerInfo(receiptAdapter,body,factory);
        body.setType(Resources.PRINTER.getStringISO88591("RECEIPTTYPE_" +receiptAdapter.getAdaptee().getType().toString()));
        body.setHeader(createReceiptBodyHeader(receiptAdapter,factory));
        List<ReceiptBodyEntry> records = receiptAdapter.getAdaptee().getRecords().stream().map((record) ->{
            ReceiptBodyEntry entry = factory.createReceiptBodyEntry();
            entry.setName(record.getName());
            entry.setQtyPrice(BigInteger.valueOf(record.getSalePrice()));
            entry.setQty(record.getSoldQuantity());
            entry.setTotal(BigInteger.valueOf((int)(record.getSoldQuantity() * record.getSalePrice())));
            return entry;
        }).collect(Collectors.toList());
        body.getEntry().addAll(records);
        body.setFooter(createReceiptBodyFooter(receiptAdapter,factory));
        return body;
    }

    private static ReceiptBodyHeader createReceiptBodyHeader(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBodyHeader header = factory.createReceiptBodyHeader();
        header.setNameHeader(Resources.PRINTER.getStringISO88591("NameHeader"));
        header.setQtyHeader(Resources.PRINTER.getStringISO88591("QtyHeader"));
        header.setQtyPriceHeader(Resources.PRINTER.getStringISO88591("QtyPriceHeader"));
        header.setTotalHeader(Resources.PRINTER.getStringISO88591("TotalHeader"));
        return header;
    }
    static private TagCurrencyValue createTagCurrencyValue(ObjectFactory f,String tag,String currency,Long value){
        TagCurrencyValue tcv = f.createTagCurrencyValue();
        tcv.setTag(tag);
        tcv.setCurrency(currency);
        tcv.setValue(BigInteger.valueOf(value));
        return tcv;
    }

    static private TagValuePair createTagValue(ObjectFactory f,String tag,String value){
        TagValuePair tv = f.createTagValuePair();
        tv.setTag(tag);
        tv.setValue(value);
        return tv;
    }

    private static ReceiptBodyFooter createReceiptBodyFooter(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBodyFooter footer = factory.createReceiptBodyFooter();
        footer.setTotal(createTagCurrencyValue(factory,
                Resources.PRINTER.getStringISO88591("TotalTag"),
                Resources.PRINTER.getStringISO88591("TotalCurrency"),
                receiptAdapter.getAdaptee().getRecords().stream()
                .map(e -> e.getSalePrice() * e.getSoldQuantity())
                .reduce(0.0,(x,y)-> x + y).longValue())
        );
        //FIXME: add currency in model.Receipt
        TagValuePair paymentMethod = factory.createTagValuePair();
        paymentMethod.setTag(Resources.PRINTER.getStringISO88591("PaymentMethod"));
        paymentMethod.setValue(Resources.PRINTER.getStringISO88591("PAYMENTMETHOD_"+receiptAdapter.getAdaptee().getPaymentMethod().toString()));
        footer.setPaymentMethod(paymentMethod);
        if(receiptAdapter.getAdaptee().getPaymentMethod() == PaymentMethod.CASH) {
            footer.setTotalRounded(createTagCurrencyValue(factory,
                    Resources.PRINTER.getStringISO88591("TotalRoundedTag"),
                    Resources.PRINTER.getStringISO88591("TotalRoundedCurrency"),
                    footer.getTotal().getValue().longValue())
            );
        }
        return footer;
    }
}
