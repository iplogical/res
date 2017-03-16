package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;

import javax.persistence.EntityManager;

/**
 * Created by Bálint on 2017.03.13..
 */
public class ReceiptRecordAdapter extends AbstractAdapter<ReceiptRecord> {

    public ReceiptRecordAdapter(ReceiptRecord adaptee, EntityManager manager) {
        super(adaptee, manager);
    }
}
