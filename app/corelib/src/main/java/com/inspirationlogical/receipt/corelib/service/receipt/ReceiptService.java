package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.util.Collection;
import java.util.List;

public interface ReceiptService {

    void sellProduct(ProductAdapter productAdapter, int amount, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(AdHocProductParams adHocProductParams, boolean takeAway);

    ReceiptRecordAdapter sellGameFee(int quantity);

    void addStockRecords(List<StockParams> paramsList);

    Collection<ReceiptRecordAdapter> getSoldProducts();

    void close(Receipt receipt, PaymentParams paymentParams);

    void paySelective(Receipt receipt, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams);

    ReceiptRecordAdapter cloneReceiptRecordAdapter(ReceiptRecordAdapter record, double amount);

    void cancelReceiptRecord(ReceiptRecordAdapter receiptRecordAdapter);

    void mergeReceiptRecords(ReceiptView receiptView);

    void setSumValues(ReceiptView receiptView);
}
