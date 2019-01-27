package com.inspirationlogical.receipt.corelib.service.receipt_record;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;

public interface ReceiptRecordService {

    ReceiptRecordView increaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount, boolean isSale);

    ReceiptRecordView decreaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount);

    void cancelReceiptRecord(ReceiptRecordView receiptRecordView);

    ReceiptRecordView cloneReceiptRecord(ReceiptRecordView record, double quantity);

    ReceiptRecordView decreaseReceiptRecord(ReceiptRecordView receiptRecordView, double quantity);
}
