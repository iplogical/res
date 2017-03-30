package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

/**
 * Created by Bálint on 2017.03.15..
 */
public class ReceiptRecordViewImpl extends AbstractModelViewImpl<ReceiptRecordAdapter>
    implements ReceiptRecordView {

    public ReceiptRecordViewImpl(ReceiptRecordAdapter adapter) {
        super(adapter);
    }

    @Override
    public Long getId() {
        return adapter.getAdaptee().getId();
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public double getSoldQuantity() {
        return adapter.getAdaptee().getSoldQuantity();
    }

    @Override
    public int getSalePrice() {
        return adapter.getAdaptee().getSalePrice();
    }

    @Override
    public int getTotalPrice() {
        return (int)(adapter.getAdaptee().getSalePrice() * adapter.getAdaptee().getSoldQuantity());
    }

    @Override
    public double getDiscountPercent() {
        return adapter.getAdaptee().getDiscountPercent();
    }

    @Override
    public double getVat() {
        return adapter.getAdaptee().getVAT();
    }

    @Override
    public void increaseSoldQuantity(double amount) {
        GuardedTransaction.Run(() -> adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() + amount)));
    }

    @Override
    public void decreaseSoldQuantity(double amount) {
        GuardedTransaction.Run(() -> adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() - amount)));
    }

    @Override
    public boolean isPartiallyPayable() {
        return adapter.getAdaptee().getProduct().getType().equals(ProductType.PARTIALLY_PAYABLE);
    }

    private static double roundToTwoDecimals(double number) {
        double rounded = Math.round(number * 100);
        return rounded / 100;
    }
}
