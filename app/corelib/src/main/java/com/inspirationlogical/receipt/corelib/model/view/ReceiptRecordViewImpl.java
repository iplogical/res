package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;

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

    //TODO: Create a service functions for the increase and decrease operation. The view should not manipulate the database.
    @Override
    public void increaseSoldQuantity(double amount) {
        GuardedTransaction.run(() -> adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() + amount)));
    }

    @Override
    public void decreaseSoldQuantity(double amount) {
        if(adapter.getAdaptee().getSoldQuantity() - amount <= 0)
        {
            GuardedTransaction.delete(adapter.getAdaptee(), () -> {
                adapter.getAdaptee().getOwner().getRecords().remove(adapter.getAdaptee());
                adapter.getAdaptee().setOwner(null);
            });
            return;
        }
        GuardedTransaction.run(() -> adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() - amount)));
    }

    @Override
    public boolean isPartiallyPayable() {
        return adapter.getAdaptee().getProduct().getType().equals(ProductType.PARTIALLY_PAYABLE);
    }
}
