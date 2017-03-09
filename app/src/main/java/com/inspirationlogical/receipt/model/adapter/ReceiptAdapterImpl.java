package com.inspirationlogical.receipt.model.adapter;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.Receipt;

public class ReceiptAdapterImpl extends AbstractAdapterImpl<Receipt>
    implements ReceiptAdapter {

    public ReceiptAdapterImpl(Receipt receipt, EntityManager manager) {
        super(receipt, manager);
    }
}
