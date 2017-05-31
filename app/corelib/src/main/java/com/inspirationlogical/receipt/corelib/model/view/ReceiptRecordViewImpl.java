package com.inspirationlogical.receipt.corelib.model.view;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

/**
 * Created by Bálint on 2017.03.15..
 */
public class ReceiptRecordViewImpl extends AbstractModelViewImpl<ReceiptRecordAdapter>
    implements ReceiptRecordView {

    private static final Logger logger = LoggerFactory.getLogger("com.inspirationlogical.receipt.corelib");

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
    public ReceiptRecordType getType() {
        return adapter.getAdaptee().getType();
    }

    @Override
    public double getSoldQuantity() {
        return adapter.getAdaptee().getSoldQuantity();
    }

    @Override
    public double getAbsoluteQuantity() {
        return adapter.getAdaptee().getAbsoluteQuantity();
    }

    @Override
    public int getPurchasePrice() {
        return adapter.getAdaptee().getPurchasePrice();
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
            logger.info("Increase quantity of a receiptRecord: name: {} soldQuantity: {}, amount: {}, size {}",  adapter.getAdaptee().getName(), adapter.getAdaptee().getSoldQuantity(), amount,  adapter.getAdaptee().getCreatedList().size());
            adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() + amount));
            if(isSale) {
                adapter.getAdaptee().getCreatedList().add(ReceiptRecordCreated.builder().created(now()).owner(adapter.getAdaptee()).build());
            } else {
                adapter.getAdaptee().getCreatedList().add(ReceiptRecordCreated.builder().created(now().minusMinutes(25)).owner(adapter.getAdaptee()).build());
            }
        });
    }

    @Override
    public void decreaseSoldQuantity(double amount) {
        if(adapter.getAdaptee().getSoldQuantity() - amount <= 0 || adapter.getAdaptee().getCreatedList().isEmpty())
        {
            logger.info("Delete a receiptRecord: {}", adapter.getAdaptee().getName());
            GuardedTransaction.delete(adapter.getAdaptee(), () -> {
                adapter.getAdaptee().getOwner().getRecords().remove(adapter.getAdaptee());
                adapter.getAdaptee().setOwner(null);
            });
        } else {
            int size = adapter.getAdaptee().getCreatedList().size() - 1;
            logger.info("Decrease quantity of a receiptRecord: name: {} soldQuantity: {}, amount: {}, size: {}", adapter.getAdaptee().getName(), adapter.getAdaptee().getSoldQuantity(), amount, size);
            GuardedTransaction.delete(adapter.getAdaptee().getCreatedList().get(size), () -> {
                adapter.getAdaptee().getCreatedList().get(size).setOwner(null);
                adapter.getAdaptee().getCreatedList().remove(adapter.getAdaptee().getCreatedList().get(size));
            });

            GuardedTransaction.run(() -> adapter.getAdaptee().setSoldQuantity(roundToTwoDecimals(adapter.getAdaptee().getSoldQuantity() - amount)));
        }
    }

    @Override
    public boolean isPartiallyPayable() {
        return adapter.getAdaptee().getProduct().getType().equals(ProductType.PARTIALLY_PAYABLE);
    }
}
