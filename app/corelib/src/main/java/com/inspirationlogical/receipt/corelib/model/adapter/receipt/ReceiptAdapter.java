package com.inspirationlogical.receipt.corelib.model.adapter.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.VATSerieAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.time.LocalDateTime.now;

public interface ReceiptAdapter {

    static ReceiptAdapter getOpenReceipt(int tableNumber) {
        Receipt receipt = ReceiptAdapterBase.getOpenReceipt(tableNumber);
        if(receipt == null) return null;
        return new ReceiptAdapterBase(receipt);
    }

    static ReceiptAdapter receiptAdapterFactory(ReceiptType type) {
        Receipt newReceipt = Receipt.builder()
                .type(type)
                .status(ReceiptStatus.OPEN)
                .paymentMethod(PaymentMethod.CASH)
                .openTime(now())
                .VATSerie(VATSerieAdapter.getActiveVATSerieAdapter().getAdaptee())
                .records(new ArrayList<>())
                .deliveryTime(now())
                .isDelivered(true)
                .build();
        return new ReceiptAdapterBase(newReceipt);
    }

//    void sellProduct(ProductAdapter productAdapter, int amount, boolean isTakeAway, boolean isGift);
//
//    void sellAdHocProduct(AdHocProductParams adHocProductParams, boolean takeAway);
//
//    ReceiptRecordAdapter sellGameFee(int quantity);

    void addStockRecords(List<StockParams> paramsList);

    Collection<ReceiptRecordAdapter> getSoldProducts();

//    void close(PaymentParams paymentParams);
//
//    void paySelective(Collection<ReceiptRecordView> records, PaymentParams paymentParams);
//
//    void payPartial(double partialValue, PaymentParams paymentParams);

//    ReceiptRecordAdapter cloneReceiptRecordAdapter(ReceiptRecordAdapter record, double amount);

//    void cancelReceiptRecord(ReceiptRecordAdapter receiptRecordAdapter);

//    void mergeReceiptRecords();
}
