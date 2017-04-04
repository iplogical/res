package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptRecordAdapter extends AbstractAdapter<ReceiptRecord> {

    public ReceiptRecordAdapter(ReceiptRecord adaptee) {
        super(adaptee);
    }

    public ProductAdapter getProductAdapter() {
        return new ProductAdapter(adaptee.getProduct());
    }

    public boolean isComplexProduct() {
        return !adaptee.getProduct().getRecipe().isEmpty();
    }
}
