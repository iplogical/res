package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReceiptService {

    ReceiptView getOpenReceipt(int tableNumber);

    static double getDiscountMultiplier(double discountPercent) {
        return (100D - discountPercent) / 100;
    }

    void sellProduct(TableView tableView, ProductView productView, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway);

    void sellGameFee(TableView tableView, int quantity);

    ReceiptRecordView getLatestGameFee(TableView tableView);

    void updateStock(List<StockParams> paramsList, ReceiptType receiptType);

    void close(Receipt receipt, PaymentParams paymentParams);

    void paySelective(Receipt receipt, List<ReceiptRecordView> records, PaymentParams paymentParams);

    List<ReceiptRecordView> payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams);

    void printReceiptFromSale(int number);

    void printAggregatedReceipt(DailyConsumptionModel dailyConsumptionModel);

    void setSumValues(ReceiptView receiptView);

    int getTotalPrice(int number);

    int getTotalServiceFee(int tableNumber);

    List<Integer> getTotalPriceAndServiceFee(List<ReceiptRecordView> recordViewList, PaymentParams paymentParams);

    Map<VATName, VatPriceModel> getVatPriceModelMap(List<ReceiptRecordView> paidProductViewList, PaymentParams paymentParams);

    VatCashierNumberModel getVatCashierNumberModel();

    void mergeReceiptRecords(ReceiptView receiptView);

    List<ReceiptView> getReceipts(LocalDate startDate, LocalDate endDate);
}
