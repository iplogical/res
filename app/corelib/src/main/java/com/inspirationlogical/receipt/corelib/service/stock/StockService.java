package com.inspirationlogical.receipt.corelib.service.stock;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.StockView;

import java.util.List;

public interface StockService {
    List<StockView> getStockViewListByCategory(ProductCategoryView selectedCategory);

    void increaseStock(Receipt receipt, ReceiptType receiptType);

    void decreaseStock(Receipt receipt, ReceiptType receiptType);

    void decreaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType);

    void closeLatestStockEntries();
}
