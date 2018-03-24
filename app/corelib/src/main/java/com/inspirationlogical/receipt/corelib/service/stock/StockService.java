package com.inspirationlogical.receipt.corelib.service.stock;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StockService {
    List<Stock> getItems();

    void increaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType);

    void decreaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType);

    void closeLatestStockEntries();
}
