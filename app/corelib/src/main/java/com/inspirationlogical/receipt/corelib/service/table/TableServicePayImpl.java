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
import net.bytebuddy.asm.Advice;
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

    @Autowired
    private TableServiceConfigImpl tableServiceConfig;

    @Override
    public TableView payTable(int tableNumber, PaymentParams paymentParams) {
        Table table = tableRepository.findByNumber(tableNumber);
        Receipt openReceipt = receiptRepository.getOpenReceipt(table.getNumber());
        if (openReceipt == null) {
            throw new IllegalTableStateException("Pay table for a closed table. Table number: " + table.getNumber());
        }
        receiptService.close(openReceipt, paymentParams);
        setDefaultTableParams(table);
        tableRepository.save(table);
        return tableServiceConfig.buildTableView(table);
    }

    private void setDefaultTableParams(Table table) {
        if(TableType.isVirtualTable(table.getType())) return;
        table.setName("");
        table.setGuestCount(0);
        table.setNote("");
    }

    @Override
    public void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        Receipt openReceipt = getOpenReceipt(tableView);
        receiptService.paySelective(openReceipt, records, paymentParams);
    }

    private Receipt getOpenReceipt(TableView tableView) {
        Table table = tableRepository.findByNumber(tableView.getNumber());
        Receipt openReceipt = receiptRepository.getOpenReceipt(table.getNumber());
        if (openReceipt == null) {
            throw new IllegalTableStateException("Pay selective for a closed table. Table number: " + table.getNumber());
        }
        return openReceipt;
    }

    @Override
    public void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams) {
        Receipt openReceipt = getOpenReceipt(tableView);
        receiptService.payPartial(openReceipt, partialValue, paymentParams);
    }
}
