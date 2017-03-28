package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ReceiptViewImpl extends AbstractModelViewImpl<ReceiptAdapter>
    implements ReceiptView {

    public ReceiptViewImpl(ReceiptAdapter adapter) {
        super(adapter);
    }

    @Override
    public Collection<ReceiptRecordView> getSoldProducts() {
        if(adapter == null) {
            return Collections.EMPTY_LIST;
        }
        return adapter.getSoldProducts().stream()
                .map(record -> new ReceiptRecordViewImpl(record))
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalPrice() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getTotalPrice();
    }
}
