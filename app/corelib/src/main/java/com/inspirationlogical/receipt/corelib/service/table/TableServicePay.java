package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.VatCashierNumberModel;
import com.inspirationlogical.receipt.corelib.params.VatPriceModel;

import java.util.List;
import java.util.Map;

public interface TableServicePay {

    TableView payTable(int tableNumber, PaymentParams paymentParams);

    void paySelective(TableView tableView, List<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams);

    int getTotalPrice(List<ReceiptRecordView> recordViewList);

    int getTotalServiceFee(List<ReceiptRecordView> recordViewList);

    Map<VATName, VatPriceModel> getVatPriceModelMap(List<ReceiptRecordView> paidProductViewList);

    VatCashierNumberModel getVatCashierNumberModel();
}
