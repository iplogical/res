package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import java.time.LocalDateTime;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

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
    public List<LocalDateTime> getCreated() {
        return adapter.getAdaptee().getCreatedList().stream()
                .map(receiptRecordCreated -> receiptRecordCreated.getCreated())
                .collect(toList());
    }

    //TODO: Create a service functions for the increase and decrease operation. The view should not manipulate the database.
    @Override
    public void increaseSoldQuantity(double amount, boolean isSale) {
        GuardedTransaction.run(() -> {
            adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() + amount));
            if(isSale) {
                adapter.getAdaptee().getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(adapter.getAdaptee()).build());
            }
        });
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
        int size = adapter.getAdaptee().getCreatedList().size() - 1;
        GuardedTransaction.delete(adapter.getAdaptee().getCreatedList().get(size), () -> {
            adapter.getAdaptee().getCreatedList().get(size).setOwner(null);
            adapter.getAdaptee().getCreatedList().remove(adapter.getAdaptee().getCreatedList().get(size));
        });

        GuardedTransaction.run(() -> adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() - amount)));
    }

    @Override
    public boolean isPartiallyPayable() {
        return adapter.getAdaptee().getProduct().getType().equals(ProductType.PARTIALLY_PAYABLE);
    }
}
