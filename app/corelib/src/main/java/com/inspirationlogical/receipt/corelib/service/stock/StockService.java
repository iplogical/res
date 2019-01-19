package com.inspirationlogical.receipt.corelib.service.stock;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.StockView;
import org.springframework.stereotype.Service;

import java.util.List;

public interface StockService {
    List<StockView> getItems();

    void increaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType);

    void decreaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType);

    void closeLatestStockEntries();
}
