package com.inspirationlogical.receipt.corelib.service.receipt_record;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;

public interface ReceiptRecordService {

    void increaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount, boolean isSale);

    void decreaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount);

    void cancelReceiptRecord(ReceiptRecordView receiptRecordView);

    ReceiptRecordView cloneReceiptRecordAdapter(ReceiptRecordView record, double quantity);
}
