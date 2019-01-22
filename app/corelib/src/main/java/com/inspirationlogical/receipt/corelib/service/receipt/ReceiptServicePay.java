package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Address;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.ReceiptPrintModel;
import com.inspirationlogical.receipt.corelib.params.ReceiptRecordPrintModel;
import com.inspirationlogical.receipt.corelib.printing.ReceiptPrinter;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import com.inspirationlogical.receipt.corelib.service.vat.VATService;
import com.inspirationlogical.receipt.corelib.utility.Round;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService.getDiscountMultiplier;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class ReceiptServicePay {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private VATService vatService;

    @Autowired
    private DailyClosureService dailyClosureService;

    @Autowired
    private StockService stockService;

    @Autowired
    private ReceiptPrinter receiptPrinter;

    public void close(Receipt receipt, PaymentParams paymentParams) {
        if(receipt.getRecords().isEmpty()) {
            deleteReceipt(receipt);
            return;
        }
        checkIfReceiptIsClosed(receipt);
        receipt.setStatus(ReceiptStatus.CLOSED);
        receipt.setClosureTime(now());
        setSumValues(receipt);
        receipt.setDiscountPercent(calculateDiscount(receipt, paymentParams));
        receipt.setUserCode(paymentParams.getUserCode());
        receipt.setPaymentMethod(paymentParams.getPaymentMethod());
        applyDiscountOnSalePrices(receipt);
        dailyClosureService.update(receipt);
        receipt.getRecords().forEach(receiptRecord -> {
            stockService.increaseStock(receiptRecord, receipt.getType());
        });
        printReceipt(receipt, paymentParams.isDoublePrint());
        receiptRepository.save(receipt);
    }

    private void printReceipt(Receipt receipt, boolean doublePrint) {
        ReceiptPrintModel receiptPrintModel = buildReceiptPrintModel(receipt);
        receiptPrinter.printReceipt(receiptPrintModel);
        if(doublePrint) {
            receiptPrinter.printReceipt(receiptPrintModel);
        }
    }

    private void deleteReceipt(Receipt receipt) {
        receipt.getOwner().getReceipts().remove(receipt);
        receipt.setOwner(null);
        receiptRepository.delete(receipt);
    }

    private void checkIfReceiptIsClosed(Receipt receipt) {
        if(receipt.getStatus() == ReceiptStatus.CLOSED) {
            throw new IllegalReceiptStateException("Close operation is illegal for a CLOSED receipt");
        }
    }

    private double calculateDiscount(Receipt receipt, PaymentParams paymentParams) {
        if(paymentParams.getDiscountPercent() != 0) {
            return paymentParams.getDiscountPercent();
        } else if(paymentParams.getDiscountAbsolute() != 0) {
            double discountAbs = paymentParams.getDiscountAbsolute();
            double sumSale = receipt.getSumSaleGrossPrice();
            double discount = discountAbs / sumSale * 100;
            return discount;
        } else return 0;
    }

    private void applyDiscountOnSalePrices(Receipt receipt) {
        receipt.setSumSaleGrossOriginalPrice(receipt.getSumSaleGrossPrice());
        receipt.setSumSaleNetOriginalPrice(receipt.getSumSaleNetPrice());
        receipt.setSumSaleGrossPrice((int)(receipt.getSumSaleGrossPrice() * getDiscountMultiplier(receipt.getDiscountPercent())));
        receipt.setSumSaleNetPrice((int)(receipt.getSumSaleNetPrice() * getDiscountMultiplier(receipt.getDiscountPercent())));
    }

    private  ReceiptPrintModel buildReceiptPrintModel(Receipt receipt) {
        Restaurant restaurant = receipt.getOwner().getOwner();
        ReceiptPrintModel receiptPrintModel = buildReceiptPrintModel(receipt, restaurant);
        List<ReceiptRecordPrintModel> receiptRecordPrintModels = buildReceiptRecordPrintModels(receipt);
        receiptPrintModel.getReceiptRecordsPrintModels().addAll(receiptRecordPrintModels);
        return receiptPrintModel;
    }

    private ReceiptPrintModel buildReceiptPrintModel(Receipt receipt, Restaurant restaurant) {
        return ReceiptPrintModel.builder()
                .restaurantName(restaurant.getRestaurantName())
                .restaurantAddress(getRestaurantAddress(restaurant.getRestaurantAddress()))
                .restaurantSocialMediaInfo(restaurant.getSocialMediaInfo())
                .restaurantWebsite(restaurant.getWebSite())
                .restaurantPhoneNumber(restaurant.getPhoneNumber())
                .receiptRecordsPrintModels(new ArrayList<>())
                .receiptType(receipt.getType().toString().toUpperCase())
                .totalPrice(receipt.getSumSaleGrossOriginalPrice())
                .totalDiscount(receipt.getSumSaleGrossOriginalPrice() - receipt.getSumSaleGrossPrice())
                .discountedTotalPrice(receipt.getSumSaleGrossPrice())
                .roundedTotalPrice(receipt.getSumSaleGrossPrice())
                .paymentMethod(receipt.getPaymentMethod().toI18nString())
                .receiptDisclaimer(restaurant.getReceiptDisclaimer())
                .receiptNote(restaurant.getReceiptNote())
                .receiptGreet(restaurant.getReceiptGreet())
                .closureTime(receipt.getClosureTime())
                .receiptId(receipt.getId())
                .build();
    }

    private String getRestaurantAddress(Address address) {
        return address.getZIPCode() + " " + address.getCity() + ", " + address.getStreet();
    }

    private List<ReceiptRecordPrintModel> buildReceiptRecordPrintModels(Receipt receipt) {
        return receipt.getRecords().stream()
                .map(this::buildReceiptRecordPrintModel).collect(Collectors.toList());
    }

    private ReceiptRecordPrintModel buildReceiptRecordPrintModel(ReceiptRecord record) {
        return ReceiptRecordPrintModel.builder()
                .productName(record.getName())
                .soldQuantity(record.getSoldQuantity())
                .productPrice(record.getSalePrice())
                .totalPrice((int) (record.getSalePrice() * record.getSoldQuantity()))
                .discount(record.getSalePrice() != record.getOriginalSalePrice())
                .build();
    }

    public void paySelective(Receipt receipt, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        Receipt paidReceipt = buildReceipt(ReceiptType.SALE);

        Map<Long, ReceiptRecordView> recordsToPay = records.stream()
                .collect(Collectors.toMap(ReceiptRecordView::getId, Function.identity()));
        List<ReceiptRecord> notPaidRecords = receipt.getRecords().stream()
                .filter(record -> !recordsToPay.containsKey(record.getId()))
                .collect(toList());
        List<ReceiptRecord> paidRecords = receipt.getRecords().stream()
                .filter(record -> recordsToPay.containsKey(record.getId()))
                .collect(toList());
        notPaidRecords.forEach(record -> record.setOwner(receipt));
        receipt.setRecords(notPaidRecords);

        paidReceipt.setRecords(paidRecords);
        paidRecords.forEach(record -> record.setOwner(paidReceipt));
        paidReceipt.setStatus(ReceiptStatus.PENDING);
        paidReceipt.setOwner(receipt.getOwner());
        receipt.getOwner().getReceipts().add(paidReceipt);
        receiptRepository.save(receipt);
        close(paidReceipt, paymentParams);
    }

    private Receipt buildReceipt(ReceiptType type) {
        return Receipt.builder()
                .type(type)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(now())
                .VATSerie(vatService.findValidVATSerie())
                .records(new ArrayList<>())
                .build();
    }

    public void payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams) {
        Receipt partialReceipt = buildReceipt(ReceiptType.SALE);
        cloneReceiptRecords(receipt, partialReceipt, partialValue);
        receipt.getRecords().forEach(record -> record.setSoldQuantity(Round.roundToTwoDecimals(record.getSoldQuantity() * (1 - partialValue))));
        partialReceipt.setOwner(receipt.getOwner());
        receiptRepository.save(receipt);
        close(partialReceipt, paymentParams);
    }

    private void cloneReceiptRecords(Receipt receipt, Receipt partialReceipt, double partialValue) {
        receipt.getRecords().forEach(record -> {
            ReceiptRecord newRecord = ReceiptRecord.cloneReceiptRecord(record);
            newRecord.setSoldQuantity(Round.roundToTwoDecimals(newRecord.getSoldQuantity() * partialValue));
            newRecord.setOwner(partialReceipt);
            partialReceipt.getRecords().add(newRecord);
//            List<Product> products = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME, query ->
//                    query.setParameter("longName", record.getProduct().getLongName()));
//            newRecord.setProduct(products.get(0));
            newRecord.setProduct(record.getProduct());
        });
    }

    public void setSumValues(ReceiptView receiptView) {
        Receipt receipt = receiptRepository.getOne(receiptView.getId());
        setSumValues(receipt);
        receiptRepository.save(receipt);
    }

    private void setSumValues(Receipt receipt) {
        receipt.setSumPurchaseGrossPrice(getSumValue(receipt, this::calculatePurchaseGrossPrice));
        receipt.setSumPurchaseNetPrice(getSumValue(receipt, this::calculatePurchaseNetPrice));
        receipt.setSumSaleGrossPrice(getSumValue(receipt, this::calculateSaleGrossPrice));
        receipt.setSumSaleNetPrice(getSumValue(receipt, this::calculateSaleNetPrice));
    }

    private int getSumValue(Receipt receipt, ToDoubleFunction<ReceiptRecord> calculator) {
        return (int)receipt.getRecords().stream().mapToDouble(calculator).sum();
    }

    private double calculatePurchaseGrossPrice(ReceiptRecord record) {
        return record.getPurchasePrice() * record.getSoldQuantity();
    }

    private double calculatePurchaseNetPrice(ReceiptRecord record) {
        return calculatePurchaseGrossPrice(record) / calculateVATDivider(record);
    }

    private double calculateSaleGrossPrice(ReceiptRecord record) {
        return record.getSalePrice() * record.getSoldQuantity();
    }

    private double calculateSaleNetPrice(ReceiptRecord record) {
        return calculateSaleGrossPrice(record) / calculateVATDivider(record);
    }

    private double calculateVATDivider(ReceiptRecord record) {
        return (100 + record.getVAT()) / 100;
    }

}
