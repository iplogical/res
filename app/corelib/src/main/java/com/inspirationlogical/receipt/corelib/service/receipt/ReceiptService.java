package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ReceiptService {

    ReceiptView getOpenReceipt(int tableNumber);

    static double getDiscountMultiplier(double discountPercent) {
        return (100D - discountPercent) / 100;
    }

    void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway);

    void sellGameFee(TableView tableView, int quantity);

    ReceiptRecordView getLatestGameFee(TableView tableView);

    void updateStock(List<StockParams> paramsList, ReceiptType receiptType, StockListener.StockUpdateListener listener);

    void close(Receipt receipt, PaymentParams paymentParams);

    void paySelective(Receipt receipt, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams);

    void setSumValues(ReceiptView receiptView);

    void mergeReceiptRecords(ReceiptView receiptView);

    List<ReceiptView> getReceipts(LocalDate startDate, LocalDate endDate);
}
