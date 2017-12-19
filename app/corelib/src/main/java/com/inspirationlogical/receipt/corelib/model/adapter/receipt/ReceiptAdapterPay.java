package com.inspirationlogical.receipt.corelib.model.adapter.receipt;

import com.inspirationlogical.receipt.corelib.exception.IllegalReceiptStateException;
import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATSerieAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.utility.Round;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase.getDiscountMultiplier;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class ReceiptAdapterPay extends AbstractAdapter<Receipt> {

    public  interface Listener{
        void onOpen(ReceiptAdapterPay receipt);
        void onClose(Receipt receipt);
    }

    public ReceiptAdapterPay(Receipt receipt) {
        super(receipt);
    }

    public void close(PaymentParams paymentParams) {
        if(adaptee.getRecords().isEmpty()) {
            deleteReceipt();
            return;
        }
        GuardedTransaction.run(() -> {
            checkIfReceiptIsClosed();
            adaptee.setStatus(ReceiptStatus.CLOSED);
            adaptee.setClosureTime(now());
            adaptee.setSumPurchaseGrossPrice(getSumValue(this::calculatePurchaseGrossPrice));
            adaptee.setSumPurchaseNetPrice(getSumValue(this::calculatePurchaseNetPrice));
            adaptee.setSumSaleGrossPrice(getSumValue(this::calculateSaleGrossPrice));
            adaptee.setSumSaleNetPrice(getSumValue(this::calculateSaleNetPrice));
            adaptee.setDiscountPercent(calculateDiscount(paymentParams));
            adaptee.setUserCode(paymentParams.getUserCode());
            adaptee.setPaymentMethod(paymentParams.getPaymentMethod());
            applyDiscountOnSalePrices();
            ReceiptAdapterListeners.getAllListeners().forEach((l) -> {l.onClose(adaptee);});
            if(paymentParams.isDoublePrint()) {
                ReceiptAdapterListeners.getPrinterListener().onClose(adaptee);
            }
        });
    }

    private void deleteReceipt() {
        GuardedTransaction.delete(adaptee, () -> {
            adaptee.getOwner().getReceipts().remove(adaptee);
            adaptee.setOwner(null);
        });
    }

    private void checkIfReceiptIsClosed() {
        if(adaptee.getStatus() == ReceiptStatus.CLOSED) {
            throw new IllegalReceiptStateException("Close operation is illegal for a CLOSED receipt");
        }
    }

    private int getSumValue(ToDoubleFunction<ReceiptRecord> calculator) {
        return (int)adaptee.getRecords().stream().mapToDouble(calculator).sum();
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

    private double calculateDiscount(PaymentParams paymentParams) {
        if(paymentParams.getDiscountPercent() != 0) {
            return paymentParams.getDiscountPercent();
        } else if(paymentParams.getDiscountAbsolute() != 0) {
            double discountAbs = paymentParams.getDiscountAbsolute();
            double sumSale = adaptee.getSumSaleGrossPrice();
            double discount = discountAbs / sumSale * 100;
            return discount;
        } else return 0;
    }

    private void applyDiscountOnSalePrices() {
        adaptee.setSumSaleGrossOriginalPrice(adaptee.getSumSaleGrossPrice());
        adaptee.setSumSaleNetOriginalPrice(adaptee.getSumSaleNetPrice());
        adaptee.setSumSaleGrossPrice((int)(adaptee.getSumSaleGrossPrice() * getDiscountMultiplier(adaptee.getDiscountPercent())));
        adaptee.setSumSaleNetPrice((int)(adaptee.getSumSaleNetPrice() * getDiscountMultiplier(adaptee.getDiscountPercent())));
    }

    public void paySelective(Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        ReceiptAdapterPay paidReceipt = buildReceipt(ReceiptType.SALE);

        GuardedTransaction.run(() -> {
            Map<Long, ReceiptRecordView> recordsToPay = records.stream()
                    .collect(Collectors.toMap(ReceiptRecordView::getId, Function.identity()));
            List<ReceiptRecord> notPaidRecords = adaptee.getRecords().stream()
                    .filter(record -> !recordsToPay.containsKey(record.getId()))
                    .collect(toList());
            List<ReceiptRecord> paidRecords = adaptee.getRecords().stream()
                    .filter(record -> recordsToPay.containsKey(record.getId()))
                    .collect(toList());
            notPaidRecords.forEach(record -> record.setOwner(adaptee));
            adaptee.setRecords(notPaidRecords);

            paidReceipt.getAdaptee().setRecords(paidRecords);
            paidRecords.forEach(record -> record.setOwner(paidReceipt.getAdaptee()));
            paidReceipt.getAdaptee().setStatus(ReceiptStatus.PENDING);
            paidReceipt.getAdaptee().setOwner(adaptee.getOwner());
            adaptee.getOwner().getReceipts().add(paidReceipt.getAdaptee());
            GuardedTransaction.persist(paidReceipt.adaptee);
        });
        paidReceipt.close(paymentParams);
    }

    public void payPartial(double partialValue, PaymentParams paymentParams) {
        ReceiptAdapterPay partialReceipt = buildReceipt(ReceiptType.SALE);
        GuardedTransaction.run(() -> {
            cloneReceiptRecords(partialReceipt.getAdaptee(), partialValue);
            adaptee.getRecords().forEach(record -> record.setSoldQuantity(Round.roundToTwoDecimals(record.getSoldQuantity() * (1 - partialValue))));
            partialReceipt.getAdaptee().setOwner(adaptee.getOwner());
            GuardedTransaction.persist(partialReceipt.getAdaptee());
        });
        partialReceipt.close(paymentParams);
    }

    private ReceiptAdapterPay buildReceipt(ReceiptType type) {
        return new ReceiptAdapterPay(Receipt.builder()
                .type(type)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(now())
                .VATSerie(VATSerieAdapter.getActiveVATSerieAdapter().getAdaptee())
                .records(new ArrayList<>())
                .build());
    }


    private void cloneReceiptRecords(Receipt newReceipt, double partialValue) {
        adaptee.getRecords().forEach(record -> {
            ReceiptRecord newRecord = ReceiptRecord.cloneReceiptRecord(record);
            newRecord.setSoldQuantity(Round.roundToTwoDecimals(newRecord.getSoldQuantity() * partialValue));
            newRecord.setOwner(newReceipt);
            newReceipt.getRecords().add(newRecord);
            List<Product> products = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME, query ->
                    query.setParameter("longName", record.getProduct().getLongName()));
            newRecord.setProduct(products.get(0));
        });
    }

    private static double calculateVATDivider(ReceiptRecord record) {
        return (100 + record.getVAT()) / 100;
    }
}
