package com.inspirationlogical.receipt.corelib.model.adapter.receipt;

import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class ReceiptAdapterMerge extends AbstractAdapter<Receipt> {

    public ReceiptAdapterMerge(Receipt adaptee) {
        super(adaptee);
    }

    public void mergeReceiptRecords() {
        GuardedTransaction.run(() -> {
            Map<Double, Map<String, List<ReceiptRecord>>> receiptsByDiscountAndName = groupRecordsByDiscountAndName();
            List<Map<String, List<ReceiptRecord>>> listOfReceiptsByDiscountAndName = new ArrayList<>(receiptsByDiscountAndName.values());
            List<ReceiptRecord> mergedRecords = new ArrayList<>();

            listOfReceiptsByDiscountAndName.forEach(stringListMap -> {
                stringListMap.values().forEach(equivalentRecords -> {
                    if (equivalentRecords.size() > 1) {
                        ReceiptRecord mergedRecord = reduceEquivalentRecords(equivalentRecords);
                        mergedRecords.add(mergedRecord);
                    }
                });
            });
            mergedRecords.forEach(receiptRecord -> adaptee.getRecords().add(receiptRecord));
        });
    }

    private Map<Double, Map<String, List<ReceiptRecord>>> groupRecordsByDiscountAndName() {
        return adaptee.getRecords().stream().collect(groupingBy(ReceiptRecord::getDiscountPercent, groupingBy(ReceiptRecord::getName)));
    }

    private ReceiptRecord reduceEquivalentRecords(List<ReceiptRecord> group) {
        return group.stream().reduce((a, b) -> {
            cancelReceiptRecord(new ReceiptRecordAdapter(a));
            cancelReceiptRecord(new ReceiptRecordAdapter(b));
            ReceiptRecord receiptRecord = buildMergedRecord(a, b);
            receiptRecord.getCreatedList().addAll(a.getCreatedList());
            receiptRecord.getCreatedList().addAll(b.getCreatedList());
            receiptRecord.getCreatedList().forEach(created -> created.setOwner(receiptRecord));
            return receiptRecord;
        }).get();
    }

    private ReceiptRecord buildMergedRecord(ReceiptRecord a, ReceiptRecord b) {
        return ReceiptRecord.builder()
                .product(a.getProduct())
                .type(a.getType())
                .name(a.getName())
                .soldQuantity(a.getSoldQuantity() + b.getSoldQuantity())
                .absoluteQuantity(a.getAbsoluteQuantity() + b.getAbsoluteQuantity())
                .purchasePrice(a.getPurchasePrice())
                .salePrice(a.getSalePrice())
                .VAT(a.getVAT())
                .discountPercent(a.getDiscountPercent())
                .owner(adaptee)
                .createdList(new ArrayList<>())
                .build();
    }

    private void cancelReceiptRecord(ReceiptRecordAdapter receiptRecordAdapter) {
        GuardedTransaction.delete(receiptRecordAdapter.getAdaptee(), () -> {
            adaptee.getRecords().remove(receiptRecordAdapter.getAdaptee());
        });
    }
}
