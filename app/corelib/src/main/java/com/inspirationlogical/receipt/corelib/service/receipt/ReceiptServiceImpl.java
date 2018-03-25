package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    private ReceiptServiceMerge receiptServiceMerge;

    @Autowired
    private ReceiptServicePay receiptServicePay;

    @Autowired
    private ReceiptServiceSell receiptServiceSell;

    @Autowired
    private ReceiptServiceStock receiptServiceStock;

    @Override
    public void sellProduct(ProductAdapter productAdapter, int amount, boolean isTakeAway, boolean isGift) {

    }

    @Override
    public void sellAdHocProduct(AdHocProductParams adHocProductParams, boolean takeAway) {

    }

    @Override
    public ReceiptRecordAdapter sellGameFee(int quantity) {
        return null;
    }

    @Override
    public void addStockRecords(List<StockParams> paramsList) {
        receiptServiceStock.addStockRecords();
    }

    @Override
    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        return null;
    }

    @Override
    public void close(Receipt receipt, PaymentParams paymentParams) {
        receiptServicePay.close(receipt, paymentParams);
    }

    @Override
    public void paySelective(Receipt receipt, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        receiptServicePay.paySelective(receipt, records, paymentParams);
    }

    @Override
    public void payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams) {
        receiptServicePay.payPartial(receipt, partialValue, paymentParams);
    }

    @Override
    public ReceiptRecordAdapter cloneReceiptRecordAdapter(ReceiptRecordAdapter record, double amount) {
        return null;
    }

    @Override
    public void cancelReceiptRecord(ReceiptRecordAdapter receiptRecordAdapter) {

    }

    @Override
    public void mergeReceiptRecords(ReceiptView receiptView) {
        receiptServiceMerge.mergeReceiptRecords(receiptView);
    }

    @Override
    public void setSumValues(ReceiptView receiptView) {
        receiptServicePay.setSumValues(receiptView);
    }
}
