package com.inspirationlogical.receipt.corelib.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.inspirationlogical.receipt.corelib.jaxb.CustomerInfo;
import com.inspirationlogical.receipt.corelib.jaxb.ObjectFactory;
import com.inspirationlogical.receipt.corelib.jaxb.Receipt;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBody;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBodyEntry;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBodyFooter;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptBodyHeader;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptFooter;
import com.inspirationlogical.receipt.corelib.jaxb.ReceiptHeader;
import com.inspirationlogical.receipt.corelib.jaxb.TagCurrencyValue;
import com.inspirationlogical.receipt.corelib.jaxb.TagValuePair;
import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.xml.sax.SAXException;


/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class ReceiptToXML {
    // TODO: Change database encoding to UTF-8. Use UTF-8 everywhere.
    public static Receipt Convert(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        return createReceipt(receipt, new ObjectFactory());
    }

    private static Schema schema;

    static {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            schema = factory.newSchema(new StreamSource(classloader.getResourceAsStream("schema/receipt.xsd")));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream ConvertToStream(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        Receipt r = createReceipt(receipt, new ObjectFactory());
        try {
            JAXBContext context = JAXBContext.newInstance("com.inspirationlogical.receipt.corelib.jaxb");
            Marshaller jaxbMarshaller = context.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            jaxbMarshaller.setSchema(schema);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            jaxbMarshaller.marshal(r, baos);
            return new ByteArrayInputStream(baos.toByteArray(), 0, baos.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Receipt createReceipt(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ObjectFactory factory) {
        Receipt receiptJAXB = factory.createReceipt();
        receiptJAXB.setHeader(createHeader(receipt, factory));
        receiptJAXB.setBody(createReceiptBody(receipt, factory));
        receiptJAXB.setFooter(createFooter(receipt, factory));
        return receiptJAXB;
    }

    private static ReceiptFooter createFooter(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ObjectFactory factory) {
        ReceiptFooter footer = factory.createReceiptFooter();
        Restaurant restaurant = receipt.getOwner().getOwner();
        setOptionalString(footer::setDisclaimer,restaurant.getReceiptDisclaimer());
        setOptionalString(footer::setNote, restaurant.getReceiptNote());
        setOptionalString(footer::setGreet, restaurant.getReceiptGreet());
        GregorianCalendar gc = GregorianCalendar.from(receipt.getClosureTime().atZone(ZoneId.systemDefault()));
        footer.setDatetime(new XMLGregorianCalendarImpl(gc));
        footer.setReceiptIdTag(Resources.PRINTER.getString("ReceipIDTag") + ":" + receipt.getId().toString());
        footer.setVendorInfo(Resources.PRINTER.getString("VendorInfo"));
        return footer;
    }


    @FunctionalInterface
    private interface Transformer<To, From> {
        To transform(From from);
    }

    @FunctionalInterface
    private interface Predicate<T> {
        boolean apply(T t);
    }

    private static void setOptionalString(Consumer<String> c, String str) {
        setOptionalString(c, str, x -> x);
    }

    private static <T> void setOptionalString(Consumer<T> c, String str, Transformer<T, String> t) {
        setOptional(c, str, x -> x != null && !x.trim().isEmpty(), t);
    }

    private static <T, F> void setOptional(
            Consumer<T> out,
            F in,
            Predicate<F> predicate,
            Transformer<T, F> transformer) {
        if (predicate.apply(in)) {
            out.accept(transformer.transform(in));
        }
    }


    private static ReceiptHeader createHeader(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ObjectFactory factory) {
        ReceiptHeader header = factory.createReceiptHeader();
        Restaurant restaurant = receipt.getOwner().getOwner();
        header.setRestaurantLogoPath(Resources.CONFIG.getString("ReceiptLogoPath"));
        header.setRestaurantName(restaurant.getRestaurantName());
        header.setRestaurantAddress(restaurant.getRestaurantAddress().getZIPCode() + " "
                                    + restaurant.getRestaurantAddress().getCity() + ", "
                                    + restaurant.getRestaurantAddress().getStreet());
        setOptionalString(header::setRestaurantSocialMediaInfo, restaurant.getSocialMediaInfo());
        setOptionalString(header::setRestaurantWebsite, restaurant.getWebSite());
        setOptionalString(header::setRestaurantPhoneNumber, restaurant.getPhoneNumber());
        return header;
    }

    private static void setCustomerInfo(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ReceiptBody body, ObjectFactory factory) {
        Client client = receipt.getClient();
        if (client != null) {
            CustomerInfo customer = factory.createCustomerInfo();
            setOptionalString(customer::setName, client.getName()
                    , x -> createTagValue(factory, Resources.PRINTER.getString("CustomerName"), x));
            setOptionalString(customer::setAddress, client.getAddress()
                    , x -> createTagValue(factory, Resources.PRINTER.getString("CustomerAddress"), x));
            setOptionalString(customer::setTaxNumber, client.getTAXNumber()
                    , x -> createTagValue(factory, Resources.PRINTER.getString("CustomerTAXnumber"), x));
            body.setCustomer(customer);
        }
    }

    private static ReceiptBody createReceiptBody(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ObjectFactory factory) {
        ReceiptBody body = factory.createReceiptBody();
        setCustomerInfo(receipt, body, factory);
        body.setType(Resources.PRINTER.getString("RECEIPTTYPE_" + receipt.getType().toString().toUpperCase()));
        body.setHeader(createReceiptBodyHeader(factory));
        List<ReceiptBodyEntry> records = receipt.getRecords().stream().map((record) -> {
            ReceiptBodyEntry entry = factory.createReceiptBodyEntry();
            String name = record.getName();
            if (record.getDiscountPercent() > 0.0) {
                // NOTE: Mark entry with '*' if it has a discount
                name += " *";
            }
            entry.setName(name);
            entry.setQtyPrice(BigInteger.valueOf(record.getSalePrice()));
            entry.setQty(record.getSoldQuantity());
            entry.setTotal(BigInteger.valueOf((int) (record.getSoldQuantity() * record.getSalePrice())));
            return entry;
        }).collect(Collectors.toList());
        body.getEntry().addAll(records);
        body.setFooter(createReceiptBodyFooter(receipt, factory));
        return body;
    }

    private static ReceiptBodyHeader createReceiptBodyHeader(ObjectFactory factory) {
        ReceiptBodyHeader header = factory.createReceiptBodyHeader();
        header.setNameHeader(Resources.PRINTER.getString("NameHeader"));
        header.setQtyHeader(Resources.PRINTER.getString("QtyHeader"));
        header.setQtyPriceHeader(Resources.PRINTER.getString("QtyPriceHeader"));
        header.setTotalHeader(Resources.PRINTER.getString("TotalHeader"));
        return header;
    }

    static private TagCurrencyValue createTagCurrencyValue(ObjectFactory f, String tag, String currency, Long value) {
        return createTagCurrencyValue(f, tag, currency, BigInteger.valueOf(value));
    }

    static private TagCurrencyValue createTagCurrencyValue(ObjectFactory f, String tag, String currency, BigInteger value) {
        TagCurrencyValue tcv = f.createTagCurrencyValue();
        tcv.setTag(tag);
        tcv.setCurrency(currency);
        tcv.setValue(value);
        return tcv;
    }

    static private TagValuePair createTagValue(ObjectFactory f, String tag, String value) {
        TagValuePair tv = f.createTagValuePair();
        tv.setTag(tag);
        tv.setValue(value);
        return tv;
    }

    private static ReceiptBodyFooter createReceiptBodyFooter(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ObjectFactory factory) {
        ReceiptBodyFooter footer = factory.createReceiptBodyFooter();
        footer.setTotal(createTagCurrencyValue(factory,
                Resources.PRINTER.getString("TotalTag"),
                Resources.PRINTER.getString("TotalCurrency"),
                receipt.getRecords().stream()
                        .map(e -> e.getSalePrice() * e.getSoldQuantity())
                        .reduce(0.0, (x, y) -> x + y).longValue())
        );

        setOptional(footer::setDiscount,
                receipt.getDiscountPercent(),
                aDouble -> aDouble > 0.0,
                aDouble -> createTagCurrencyValue(factory,
                        Resources.PRINTER.getString("DiscountTag"),
                        Resources.PRINTER.getString("TotalCurrency"),
                        // NOTE: discount is in percentage dimension  hence the div by 100.0
                       -(long) (aDouble / 100.0 * footer.getTotal().getValue().doubleValue()))
        );

        setOptional(footer::setDiscountedTotal, footer.getDiscount(),
                tagCurrencyValue -> tagCurrencyValue != null && tagCurrencyValue.getValue().intValue() != 0,
                tagCurrencyValue -> createTagCurrencyValue(factory,
                        Resources.PRINTER.getString("DiscountedTotalTag"),
                        tagCurrencyValue.getCurrency(),
                        footer.getTotal().getValue().subtract(tagCurrencyValue.getValue().abs()))
        );

        footer.setPaymentMethod(createTagValue(factory,
                Resources.PRINTER.getString("PaymentMethod"),
                Resources.PRINTER.getString("PAYMENTMETHOD_" +
                        receipt.getPaymentMethod().toString().toUpperCase())));

        //FIXME: add currency in model.Receipt
        BigInteger total = footer.getDiscountedTotal() == null
                ? footer.getTotal().getValue()
                : footer.getDiscountedTotal().getValue();

        // NOTE: set rounded total only if paymentmethod is cash, and rounded value not equals total
        setOptional(footer::setTotalRounded, receipt.getPaymentMethod(),
                paymentMethod -> RoundingLogic.roundingNeeded(paymentMethod) &&
                        !total.equals(BigInteger.valueOf((long) RoundingLogic.create(paymentMethod).round(total.doubleValue()))),
                x -> createTagCurrencyValue(factory,
                        Resources.PRINTER.getString("TotalRoundedTag"),
                        Resources.PRINTER.getString("TotalRoundedCurrency"),
                        (long) RoundingLogic.create(receipt.getPaymentMethod()).round(total.doubleValue()))
        );
        return footer;
    }
}
