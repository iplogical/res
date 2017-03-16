package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;

public class ReceiptViewImpl extends AbstractModelViewImpl<ReceiptAdapter>
    implements ReceiptView {

    public ReceiptViewImpl(ReceiptAdapter adapter) {
        super(adapter);
    }
}
