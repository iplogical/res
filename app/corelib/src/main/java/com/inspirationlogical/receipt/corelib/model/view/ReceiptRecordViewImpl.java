package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Bálint on 2017.03.15..
 */
public class ReceiptRecordViewImpl extends AbstractModelViewImpl<ReceiptRecordAdapter>
    implements ReceiptRecordView {

    private @Setter @Getter double paidQuantity;

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
}