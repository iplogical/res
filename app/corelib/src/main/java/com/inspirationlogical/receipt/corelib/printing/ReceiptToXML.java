package com.inspirationlogical.receipt.corelib.printing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import com.inspirationlogical.receipt.corelib.utility.RoundingLogic;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class ReceiptToXML {

    private final static Logger logger = LoggerFactory.getLogger(ReceiptToXML.class);

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

    @FunctionalInterface
    private interface Transformer<To, From> {
        To transform(From from);
    }

    @FunctionalInterface
    private interface Predicate<T> {
        boolean apply(T t);
    }

    private ObjectFactory factory;

    public ReceiptToXML(ObjectFactory factory) {
        this.factory = factory;
    }

    public InputStream convertToXMLStream(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        logger.info("Create the Receipt content tree from the entity: " + receipt.toString());
        Receipt r = createReceipt(receipt);
        ByteArrayOutputStream baos = null;
        try {
            logger.info("Create the JAXBContext and the Marshaller objects.");
            JAXBContext context = JAXBContext.newInstance("com.inspirationlogical.receipt.corelib.jaxb");
            Marshaller jaxbMarshaller = context.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            jaxbMarshaller.setSchema(schema);
            baos = new ByteArrayOutputStream();

            logger.info("Start marshalling to create the xml representation of the Receipt");
            jaxbMarshaller.marshal(r, baos);
            return new ByteArrayInputStream(baos.toByteArray(), 0, baos.size());
        } catch (Exception e) {
            logger.error("Exception in convertToXMLStream: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                logger.error("Error while closing the output stream", e);
            }
        }
    }

    private Receipt createReceipt(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        logger.info("Creating the JAXB receipt.");
        Receipt receiptJAXB = factory.createReceipt();
        receiptJAXB.setHeader(createHeader(receipt));
        receiptJAXB.setBody(createReceiptBody(receipt));
        receiptJAXB.setFooter(createFooter(receipt));
        return receiptJAXB;
    }

    private ReceiptHeader createHeader(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        logger.info("Creating the JAXB receipt header.");
        ReceiptHeader header = factory.createReceiptHeader();
        Restaurant restaurant = receipt.getOwner().getOwner();
        logger.info("Restaurant: " + restaurant.toString());
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

    private void setOptionalString(Consumer<String> c, String str) {
        setOptionalString(c, str, x -> x);
    }

    private <T> void setOptionalString(Consumer<T> c, String str, Transformer<T, String> t) {
        setOptional(c, str, x -> x != null && !x.trim().isEmpty(), t);
    }

    private <T, F> void setOptional(
            Consumer<T> out,
            F in,
            Predicate<F> predicate,
            Transformer<T, F> transformer) {
        if (predicate.apply(in)) {
            out.accept(transformer.transform(in));
        }
    }

    private ReceiptBody createReceiptBody(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        logger.info("Creating the JAXB receipt body.");
        ReceiptBody body = factory.createReceiptBody();
        setCustomerInfo(receipt, body);
        body.setType(Resources.PRINTER.getString("RECEIPTTYPE_" + receipt.getType().toString().toUpperCase()));
        body.setHeader(createReceiptBodyHeader());
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
        body.setFooter(createReceiptBodyFooter(receipt));
        return body;
    }

    private void setCustomerInfo(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ReceiptBody body) {
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

    private ReceiptBodyHeader createReceiptBodyHeader() {
        ReceiptBodyHeader header = factory.createReceiptBodyHeader();
        header.setNameHeader(Resources.PRINTER.getString("NameHeader"));
        header.setQtyHeader(Resources.PRINTER.getString("QtyHeader"));
        header.setQtyPriceHeader(Resources.PRINTER.getString("QtyPriceHeader"));
        header.setTotalHeader(Resources.PRINTER.getString("TotalHeader"));
        return header;
    }

    private ReceiptBodyFooter createReceiptBodyFooter(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        ReceiptBodyFooter receiptBodyFooter = factory.createReceiptBodyFooter();
        setTotalField(receipt, receiptBodyFooter);
        setDiscountField(receipt, receiptBodyFooter);
        setDiscountedTotalField(receiptBodyFooter);
        setTotalRoundedField(receipt, receiptBodyFooter);
        setPaymentMethodField(receipt, receiptBodyFooter);
        return receiptBodyFooter;
    }

    private void setTotalField(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ReceiptBodyFooter receiptBodyFooter) {
        receiptBodyFooter.setTotal(createTagCurrencyValue(factory,
                Resources.PRINTER.getString("TotalTag"),
                Resources.PRINTER.getString("TotalCurrency"),
                receipt.getRecords().stream()
                        .map(e -> e.getSalePrice() * e.getSoldQuantity())
                        .reduce(0.0, (x, y) -> x + y).longValue())
        );
    }

    private void setDiscountField(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ReceiptBodyFooter receiptBodyFooter) {
        setOptional(receiptBodyFooter::setDiscount,
                receipt.getDiscountPercent(),
                aDouble -> aDouble > 0.0,
                aDouble -> createTagCurrencyValue(factory,
                        Resources.PRINTER.getString("DiscountTag"),
                        Resources.PRINTER.getString("TotalCurrency"),
                        // NOTE: discount is in percentage dimension  hence the div by 100.0
                        -(long) (aDouble / 100.0 * receiptBodyFooter.getTotal().getValue().doubleValue()))
        );
    }

    private void setDiscountedTotalField(ReceiptBodyFooter receiptBodyFooter) {
        setOptional(receiptBodyFooter::setDiscountedTotal, receiptBodyFooter.getDiscount(),
                tagCurrencyValue -> tagCurrencyValue != null && tagCurrencyValue.getValue().intValue() != 0,
                tagCurrencyValue -> createTagCurrencyValue(factory,
                        Resources.PRINTER.getString("DiscountedTotalTag"),
                        tagCurrencyValue.getCurrency(),
                        receiptBodyFooter.getTotal().getValue().subtract(tagCurrencyValue.getValue().abs()))
        );
    }

    private void setTotalRoundedField(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ReceiptBodyFooter receiptBodyFooter) {
        //FIXME: add currency in model.Receipt
        BigInteger total = receiptBodyFooter.getDiscountedTotal() == null
                ? receiptBodyFooter.getTotal().getValue()
                : receiptBodyFooter.getDiscountedTotal().getValue();

        // NOTE: set rounded total only if payment method is cash, and rounded value not equals total
        setOptional(receiptBodyFooter::setTotalRounded, receipt.getPaymentMethod(),
                paymentMethod -> RoundingLogic.roundingNeeded(paymentMethod) &&
                        !total.equals(BigInteger.valueOf((long) RoundingLogic.create(paymentMethod).round(total.doubleValue()))),
                x -> createTagCurrencyValue(factory,
                        Resources.PRINTER.getString("TotalRoundedTag"),
                        Resources.PRINTER.getString("TotalRoundedCurrency"),
                        (long) RoundingLogic.create(receipt.getPaymentMethod()).round(total.doubleValue()))
        );
    }

    private void setPaymentMethodField(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt, ReceiptBodyFooter receiptBodyFooter) {
        receiptBodyFooter.setPaymentMethod(createTagValue(factory,
                Resources.PRINTER.getString("PaymentMethod"),
                Resources.PRINTER.getString("PAYMENTMETHOD_" +
                        receipt.getPaymentMethod().toString().toUpperCase())));
    }

    private TagCurrencyValue createTagCurrencyValue(ObjectFactory f, String tag, String currency, Long value) {
        return createTagCurrencyValue(f, tag, currency, BigInteger.valueOf(value));
    }

    private TagCurrencyValue createTagCurrencyValue(ObjectFactory f, String tag, String currency, BigInteger value) {
        TagCurrencyValue tcv = f.createTagCurrencyValue();
        tcv.setTag(tag);
        tcv.setCurrency(currency);
        tcv.setValue(value);
        return tcv;
    }

    private TagValuePair createTagValue(ObjectFactory f, String tag, String value) {
        TagValuePair tv = f.createTagValuePair();
        tv.setTag(tag);
        tv.setValue(value);
        return tv;
    }

    private ReceiptFooter createFooter(com.inspirationlogical.receipt.corelib.model.entity.Receipt receipt) {
        logger.info("Creating the JAXB receipt footer.");
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
}
