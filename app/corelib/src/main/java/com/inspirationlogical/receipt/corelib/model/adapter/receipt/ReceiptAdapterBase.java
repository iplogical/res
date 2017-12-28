package com.inspirationlogical.receipt.corelib.model.adapter.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER;
import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GRAPH_RECEIPT_AND_RECORDS;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class ReceiptAdapterBase extends AbstractAdapter<Receipt> implements ReceiptAdapter {

    private ReceiptAdapterMerge receiptAdapterMerge;
    private ReceiptAdapterPay receiptAdapterPay;
    private ReceiptAdapterSell receiptAdapterSell;
    private ReceiptAdapterStock receiptAdapterStock;

    public ReceiptAdapterBase(Receipt adaptee) {
        super(adaptee);
        receiptAdapterMerge = new ReceiptAdapterMerge(adaptee);
        receiptAdapterPay = new ReceiptAdapterPay(adaptee);
        receiptAdapterSell = new ReceiptAdapterSell(adaptee);
        receiptAdapterStock = new ReceiptAdapterStock(adaptee);
    }

    static Receipt getOpenReceipt(int tableNumber) {
        List<Receipt> adapters = getReceiptsByStatusAndOwner(ReceiptStatus.OPEN, tableNumber);
        if (adapters.size() == 0) {
            return null;
        } else if (adapters.size() > 1) {
            throw new RuntimeException();
        }
        return adapters.get(0);
    }

    public static List<Receipt> getReceiptsByStatusAndOwner(ReceiptStatus status, int tableNumber) {
        return GuardedTransaction.runNamedQuery(GET_RECEIPT_BY_STATUS_AND_OWNER, GRAPH_RECEIPT_AND_RECORDS,
                query -> {
                    query.setParameter("status", status);
                    query.setParameter("number", tableNumber);
                    return query;
                });
    }

    public static List<ReceiptAdapterBase> getReceipts() {
        List<Receipt> receipts = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPTS);
        return receipts.stream().map(ReceiptAdapterBase::new).collect(toList());
    }

    public static List<ReceiptAdapterBase> getReceiptsByClosureTime(LocalDateTime startTime, LocalDateTime endTime) {
        List<Receipt> receipts = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_CLOSURE_TIME_AND_TYPE,
                query -> query.setParameter("startTime", startTime)
                        .setParameter("endTime", endTime)
                        .setParameter("type", ReceiptType.SALE));
        return receipts.stream().map(ReceiptAdapterBase::new).collect(toList());
    }

    public static double getDiscountMultiplier(double discountPercent) {
        return (100D - discountPercent) / 100;
    }

    public static List<Object[]> getReceiptRecordsByTimeStampAndName(ProductAdapter productAdapter, LocalDateTime dateTime) {
        return GuardedTransaction.runNamedQueryWithJoin(ReceiptRecord.GET_RECEIPT_RECORDS_BY_TIMESTAMP,
                query -> query.setParameter("created", dateTime)
                        .setParameter("name", productAdapter.getAdaptee().getLongName()));
    }

    @Override
    public void sellProduct(ProductAdapter productAdapter, int amount, boolean isTakeAway, boolean isGift) {
        receiptAdapterSell.sellProduct(productAdapter, amount, isTakeAway, isGift);
    }

    @Override
    public void sellAdHocProduct(AdHocProductParams adHocProductParams, boolean takeAway) {
        receiptAdapterSell.sellAdHocProduct(adHocProductParams, takeAway);
    }

    @Override
    public ReceiptRecordAdapter sellGameFee(int quantity) {
        return receiptAdapterSell.sellGameFee(quantity);
    }

    @Override
    public ReceiptRecordAdapter cloneReceiptRecordAdapter(ReceiptRecordAdapter record, double amount) {
        return receiptAdapterSell.cloneReceiptRecordAdapter(record, amount);
    }

    @Override
    public void cancelReceiptRecord(ReceiptRecordAdapter receiptRecordAdapter) {
        receiptAdapterSell.cancelReceiptRecord(receiptRecordAdapter);
    }

    @Override
    public void addStockRecords(List<StockParams> paramsList) {
        receiptAdapterStock.addStockRecords(paramsList);
    }

    @Override
    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        List<ReceiptRecord> records = getReceiptRecords();
        return records.stream().map(ReceiptRecordAdapter::new).collect(toList());
    }

    public List<ReceiptRecordAdapter> getAggregatedRecords() {
        List<ReceiptRecord> records = adaptee.getRecords();
        return records.stream().map(ReceiptRecordAdapter::new).collect(toList());
    }

    @Override
    public void close(PaymentParams paymentParams) {
        receiptAdapterPay.close(paymentParams);
    }

    @Override
    public void paySelective(Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        receiptAdapterPay.paySelective(records, paymentParams);
    }

    @Override
    public void payPartial(double partialValue, PaymentParams paymentParams) {
        receiptAdapterPay.payPartial(partialValue, paymentParams);
    }

    @Override
    public void mergeReceiptRecords() {
        receiptAdapterMerge.mergeReceiptRecords();
    }

    public int getTotalPrice() {
        return getReceiptRecords().stream()
                .mapToInt(record -> (int)(record.getSalePrice() * record.getSoldQuantity())).sum();
    }

    private List<ReceiptRecord> getReceiptRecords() {
        return GuardedTransaction.runNamedQuery(ReceiptRecord.GET_RECEIPT_RECORDS_BY_RECEIPT,
                query -> {query.setParameter("owner_id", adaptee.getId());
                    return query;});
    }

    public LocalDateTime getLatestSellTime() {
        Optional<ReceiptRecordCreated> latest = getReceiptRecords().stream()
                .map(ReceiptRecord::getCreatedList)
                .flatMap(Collection::stream)
                .max(Comparator.comparing(ReceiptRecordCreated::getCreated));
        if(latest.isPresent()) {
            return latest.get().getCreated();
        } else {
            return now().minusDays(1);
        }
    }
}
