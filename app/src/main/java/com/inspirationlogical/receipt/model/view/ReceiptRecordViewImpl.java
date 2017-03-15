package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.model.entity.ReceiptRecord;

import javax.swing.plaf.nimbus.AbstractRegionPainter;

/**
 * Created by Bálint on 2017.03.15..
 */
public class ReceiptRecordViewImpl extends AbstractModelViewImpl<ReceiptRecordAdapter>
    implements ReceiptRecordView {

    public ReceiptRecordViewImpl(ReceiptRecordAdapter adapter) {
        super(adapter);
    }
}
