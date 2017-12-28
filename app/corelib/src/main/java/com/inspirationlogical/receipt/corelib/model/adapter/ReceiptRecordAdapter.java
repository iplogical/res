package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptRecordAdapter extends AbstractAdapter<ReceiptRecord> {

    public ReceiptRecordAdapter(ReceiptRecord adaptee) {
        super(adaptee);
    }

    public ReceiptRecordAdapter decreaseReceiptRecord(double quantity) {
        if(adaptee.getSoldQuantity() > quantity) {
            return decreaseSoldQuantity(quantity);
        } else {
            return deleteReceiptRecord();
        }
    }

    private ReceiptRecordAdapter decreaseSoldQuantity(double quantity) {
        GuardedTransaction.run(() -> {
            adaptee.setSoldQuantity(adaptee.getSoldQuantity() - quantity);
            new ReceiptAdapterPay(adaptee.getOwner()).setSumValues();
        });
        return this;
    }

    private ReceiptRecordAdapter deleteReceiptRecord() {
        GuardedTransaction.delete(adaptee, () -> {
            Receipt owner = adaptee.getOwner();
            owner.getRecords().remove(adaptee);
            adaptee.setProduct(null);
            adaptee.setOwner(null);
            new ReceiptAdapterPay(owner).setSumValues();
        });
        return null;
    }
}
