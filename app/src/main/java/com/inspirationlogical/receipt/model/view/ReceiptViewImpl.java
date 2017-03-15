package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.model.adapter.ReceiptRecordAdapter;

public class ReceiptViewImpl extends AbstractModelViewImpl<ReceiptAdapter>
    implements ReceiptView {

    public ReceiptViewImpl(ReceiptAdapter adapter) {
        super(adapter);
    }
}
