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
    public void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift) {
        receiptServiceSell.sellProduct(tableView, productView, amount, isTakeAway, isGift);
    }

    @Override
    public void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean isTakeAway) {
        receiptServiceSell.sellAdHocProduct(tableView, adHocProductParams, isTakeAway);
    }

    @Override
    public ReceiptRecordView sellGameFee(TableView tableView, int quantity) {
        return receiptServiceSell.sellGameFee(tableView, quantity);
    }

    @Override
    public void updateStock(List<StockParams> paramsList, ReceiptType receiptType, StockListener.StockUpdateListener listener) {
        receiptServiceStock.updateStock(paramsList, receiptType, listener);
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
    public void setSumValues(ReceiptView receiptView) {
        receiptServicePay.setSumValues(receiptView);
    }

    @Override
    public void mergeReceiptRecords(ReceiptView receiptView) {
        receiptServiceMerge.mergeReceiptRecords(receiptView);
    }
}
