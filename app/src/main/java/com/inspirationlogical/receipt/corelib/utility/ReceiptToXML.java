package com.inspirationlogical.receipt.corelib.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.inspirationlogical.receipt.corelib.jaxb.ObjectFactory;
import com.inspirationlogical.receipt.corelib.jaxb.Receipt;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBody;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBodyEntry;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBodyFooter;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBodyHeader;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptFooter;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptHeader;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;


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
        footer.setDisclaimer(Resources.PRINTER.getString("Disclaimer"));
        //TODO: get active note from Entity and set only if present
        footer.setNote("Árvíztűrő tükörfúrógép!");
        footer.setGreet(Resources.PRINTER.getString("Greet"));
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(receiptAdapter.getAdaptee().getClosureTime().getTimeInMillis());
        footer.setDatetime(new XMLGregorianCalendarImpl(gc));
        footer.setReceiptIdTag(Resources.PRINTER.getString("ReceipIDTag")+":"+receiptAdapter.getAdaptee().getId().toString());
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
        header.setRestaurantWebsite("www.gameuppub.com");
        return  header;
    }

    private static ReceiptBody createReceiptBody(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBody body = factory.createReceiptBody();
        body.setBodyHeader(createReceiptBodyHeader(receiptAdapter,factory));
        List<ReceiptBodyEntry> records = receiptAdapter.getAdaptee().getRecords().stream().map((record) ->{
            ReceiptBodyEntry entry = factory.createReceiptBodyEntry();
            entry.setName(record.getName());
            entry.setQtyPrice(BigInteger.valueOf(record.getSalePrice()));
            entry.setQty(BigInteger.valueOf((int)record.getSoldQuantity()));
            entry.setTotal(BigInteger.valueOf((int)record.getSoldQuantity() * record.getSalePrice()));
            return entry;
        }).collect(Collectors.toList());
        body.getEntry().addAll(records);
        body.setBodyFooter(createReceiptBodyFooter(receiptAdapter,factory));
        return body;
    }

    private static ReceiptBodyHeader createReceiptBodyHeader(ReceiptAdapter receiptAdapter, ObjectFactory factory) {
        ReceiptBodyHeader header = factory.createReceiptBodyHeader();
        header.setNameHeader(Resources.PRINTER.getString("NameHeader"));
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
                        .map(e -> e.getSalePrice() * e.getSoldQuantity())
                        .reduce(0.0,(x,y)-> x + y).intValue())
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
