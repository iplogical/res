package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptServiceMerge receiptServiceMerge;

    @Autowired
    private ReceiptServicePay receiptServicePay;

    @Autowired
    private ReceiptServiceSell receiptServiceSell;

    @Autowired
    private ReceiptServiceStock receiptServiceStock;

    @Override
    public ReceiptView getOpenReceipt(int tableNumber) {
        Receipt receipt = receiptRepository.getOpenReceipt(tableNumber);
        if (receipt == null) {
            return null;
        }
        return new ReceiptView(receipt);
    }

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
    };

    @Override
    public List<ReceiptView> getReceipts(LocalDate startDate, LocalDate endDate) {
        return receiptRepository.getReceiptsByClosureTime(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
                .stream().map(ReceiptView::new).collect(toList());
    }
}
