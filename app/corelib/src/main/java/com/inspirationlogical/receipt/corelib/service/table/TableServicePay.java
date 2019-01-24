package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;

import java.util.Collection;

public interface TableServicePay {

    TableView payTable(int tableNumber, PaymentParams paymentParams);

    void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams);
}
