package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TableServicePayImpl implements TableServicePay {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptService receiptService;

    @Override
    public void payTable(TableView tableView, PaymentParams paymentParams) {
        Table table = tableRepository.getOne(tableView.getId());
        Receipt openReceipt = receiptRepository.getOpenReceipt(table.getNumber());
        if (openReceipt == null) {
            throw new IllegalTableStateException("Pay table for a closed table. Table number: " + table.getNumber());
        }
        receiptService.close(openReceipt, paymentParams);
        setDefaultTableParams(table);
        tableRepository.save(table);
    }

    private void setDefaultTableParams(Table table) {
        if(TableType.isVirtualTable(table.getType())) return;
        table.setName("");
        table.setGuestCount(0);
        table.setNote("");
    }

    @Override
    public void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        Table table = tableRepository.getOne(tableView.getId());
        Receipt openReceipt = receiptRepository.getOpenReceipt(table.getNumber());
        if(openReceipt == null) {
            throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + table.getNumber());
        }
        receiptService.paySelective(openReceipt, records, paymentParams);
    }

    @Override
    public void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams) {
        Table table = tableRepository.getOne(tableView.getId());
        Receipt openReceipt = receiptRepository.getOpenReceipt(table.getNumber());
        if(openReceipt == null) {
            throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + table.getNumber());
        }
        receiptService.payPartial(openReceipt, partialValue, paymentParams);
    }
}
