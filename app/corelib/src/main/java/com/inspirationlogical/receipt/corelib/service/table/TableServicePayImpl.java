package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.VatPriceModel;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TableServicePayImpl implements TableServicePay {

    private static final Logger logger = LoggerFactory.getLogger(TableServicePayImpl.class);

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
        receiptService.close(openReceipt, paymentParams);
        setDefaultTableParams(table);
        tableRepository.save(table);
        logger.info("A table was paid: " + tableNumber + ", " + paymentParams);
        return tableServiceConfig.buildTableView(table);
    }

    private void setDefaultTableParams(Table table) {
        if(TableType.isVirtualTable(table.getType())) {
            return;
        }
        table.setName("");
        table.setGuestCount(0);
        table.setNote("");
    }

    @Override
    public void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        receiptService.paySelective(openReceipt, records, paymentParams);
        logger.info("A table was selectively paid: " + tableView + ", " + paymentParams);
    }

    @Override
    public void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams) {
        Receipt openReceipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        receiptService.payPartial(openReceipt, partialValue, paymentParams);
        logger.info("A table was partially paid: partialValue:" + partialValue + ", " + tableView + ", " + paymentParams);
    }

    @Override
    public int getTotalPrice(List<ReceiptRecordView> recordViewList) {
        return receiptService.getTotalPrice(recordViewList);
    }

    @Override
    public int getTotalServiceFee(List<ReceiptRecordView> recordViewList) {
        return receiptService.getTotalServiceFee(recordViewList);
    }

    @Override
    public Map<VATName, VatPriceModel> getVatPriceModelMap(List<ReceiptRecordView> paidProductViewList) {
        return receiptService.getVatPriceModelMap(paidProductViewList);
    }
}
