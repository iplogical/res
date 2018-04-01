package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.util.Collection;
import java.util.List;

public interface ReceiptService {

    static double getDiscountMultiplier(double discountPercent) {
        return (100D - discountPercent) / 100;
    }

    void sellProduct(Receipt receipt, ProductView productView, int amount, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(Receipt receipt, AdHocProductParams adHocProductParams, boolean takeAway);

    ReceiptRecordView sellGameFee(Receipt receipt, int quantity);

    void updateStock(List<StockParams> paramsList, ReceiptType receiptType, StockListener.StockUpdateListener listener);

    void close(Receipt receipt, PaymentParams paymentParams);

    void paySelective(Receipt receipt, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams);

    void setSumValues(ReceiptView receiptView);

    void mergeReceiptRecords(ReceiptView receiptView);
}