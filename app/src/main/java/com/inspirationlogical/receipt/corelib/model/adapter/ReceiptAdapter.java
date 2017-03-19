package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class ReceiptAdapter extends AbstractAdapter<Receipt> {

    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }

    public static ReceiptAdapter receiptAdapterFactory(ReceiptType type) {
        return new ReceiptAdapter(Receipt.builder()
                                        .type(type)
                                        .status(ReceiptStatus.OPEN)
                                        .paymentMethod(PaymentMethod.CASH)
                                        .openTime(new GregorianCalendar())
                                        .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
                                        .client(getDefaultClient())
                                        .build());
    }

    private static Client getDefaultClient() {
        // FIXME
        return Client.builder().name("client").address("dummy").TAXNumber("123").build();
    }

    public ReceiptAdapter(Receipt receipt) {
        super(receipt);
    }

    public void sellProduct(ProductAdapter productAdapter, int amount, PaymentParams paymentParams) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(productAdapter.getAdaptee())
                    .type(paymentParams.getReceiptRecordType())
                    .created(new GregorianCalendar())
                    .name(productAdapter.getAdaptee().getLongName())
                    .soldQuantity(amount)
                    .purchasePrice(productAdapter.getAdaptee().getPurchasePrice())
                    .salePrice(productAdapter.getAdaptee().getSalePrice())
                    .VAT(VATAdapter.getVatByName(paymentParams.getReceiptRecordType(), VATStatus.VALID).getAdaptee().getVAT())
                    // TODO: calculate discount based on the PriceModifiers.
                    //.discountPercent(paymentParams.getDiscountPercent())
                    .build();
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
            // FIXME: Persist new ReciptRecord?
        });
    }

    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        return adaptee.getRecords().stream()
                .map(receiptRecord -> new ReceiptRecordAdapter(receiptRecord))
                .collect(Collectors.toList());
    }

    public void close(PaymentParams paymentParams){
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            adaptee.setStatus(ReceiptStatus.CLOSED);
            adaptee.setPaymentMethod(paymentParams.getPaymentMethod());
            adaptee.setClosureTime(Calendar.getInstance());
            adaptee.setUserCode(paymentParams.getUserCode());
            adaptee.setSumPurchaseGrossPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculatePurchaseGrossPrice).sum());
            adaptee.setSumPurchaseNetPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculatePurchaseNetPrice).sum());
            adaptee.setSumSaleGrossPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculateSaleGrossPrice).sum());
            adaptee.setSumSaleNetPrice((int)adaptee.getRecords().stream()
                    .mapToDouble(ReceiptAdapter::calculateSaleNetPrice).sum());
            adaptee.setDiscountPercent(calculateDiscount(paymentParams));
            applyDiscountOnSalePrices();
            ReceiptAdapterListeners.getAllListeners().forEach((l) -> {l.onClose(this);});
        });
    }

    private double calculateDiscount(PaymentParams paymentParams) {
        if(paymentParams.getDiscountPercent() != 0) {
            return paymentParams.getDiscountPercent();
        } else if(paymentParams.getDiscountAbsolute() != 0) {
            double discountAbs = paymentParams.getDiscountAbsolute();
            double sumSale = getAdaptee().getSumSaleGrossPrice();
            double discount = discountAbs / sumSale * 100;
            return discount;
        } else return 0;
    }

    private void applyDiscountOnSalePrices() {
        adaptee.setSumSaleGrossPrice((int)(adaptee.getSumSaleGrossPrice() * getDiscountMultiplier(adaptee.getDiscountPercent())));
        adaptee.setSumSaleNetPrice((int)(adaptee.getSumSaleNetPrice() * getDiscountMultiplier(adaptee.getDiscountPercent())));
    }

    private static double calculatePurchaseGrossPrice(ReceiptRecord record) {
        return record.getPurchasePrice() * record.getSoldQuantity();
    }

    private static double calculatePurchaseNetPrice(ReceiptRecord record) {
        return calculatePurchaseGrossPrice(record) / calculateVATDivider(record);
    }

    private static double calculateSaleGrossPrice(ReceiptRecord record) {
        return record.getSalePrice() * record.getSoldQuantity() * getDiscountMultiplier(record.getDiscountPercent());
    }

    private static double calculateSaleNetPrice(ReceiptRecord record) {
        return calculateSaleGrossPrice(record) / calculateVATDivider(record);
    }

    private static double calculateVATDivider(ReceiptRecord record) {
        return (100 + record.getVAT()) / 100;
    }

    private static double getDiscountMultiplier(double discountPercent) {
        return (100D - discountPercent) / 100;
    }
}
