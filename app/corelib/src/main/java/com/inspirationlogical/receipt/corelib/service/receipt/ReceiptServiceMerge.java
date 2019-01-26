package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRecordRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ReceiptServiceMerge {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptRecordRepository receiptRecordRepository;

    public void mergeReceiptRecords(ReceiptView receiptView) {
        Receipt receipt = receiptRepository.findById(receiptView.getId());
        Map<Double, Map<String, List<ReceiptRecord>>> receiptsByDiscountAndName = groupRecordsByDiscountAndName(receipt);
        List<Map<String, List<ReceiptRecord>>> listOfReceiptsByDiscountAndName = new ArrayList<>(receiptsByDiscountAndName.values());
        List<ReceiptRecord> mergedRecords = new ArrayList<>();

        listOfReceiptsByDiscountAndName.forEach(stringListMap -> {
            stringListMap.values().forEach(equivalentRecords -> {
                if (equivalentRecords.size() > 1) {
                    ReceiptRecord mergedRecord = reduceEquivalentRecords(equivalentRecords, receipt);
                    mergedRecords.add(mergedRecord);
                }
            });
        });
        mergedRecords.forEach(receiptRecord -> receipt.getRecords().add(receiptRecord));
    }

    private Map<Double, Map<String, List<ReceiptRecord>>> groupRecordsByDiscountAndName(Receipt receipt) {
        return receipt.getRecords().stream().collect(groupingBy(ReceiptRecord::getDiscountPercent, groupingBy(ReceiptRecord::getName)));
    }

    private ReceiptRecord reduceEquivalentRecords(List<ReceiptRecord> group, Receipt owner) {
        return group.stream().reduce((a, b) -> {
            cancelReceiptRecord(a, owner);
            cancelReceiptRecord(b, owner);
            ReceiptRecord receiptRecord = buildMergedRecord(a, b, owner);
            receiptRecord.getCreatedList().addAll(a.getCreatedList());
            receiptRecord.getCreatedList().addAll(b.getCreatedList());
            receiptRecord.getCreatedList().forEach(created -> created.setOwner(receiptRecord));
            return receiptRecord;
        }).get();
    }

    private ReceiptRecord buildMergedRecord(ReceiptRecord a, ReceiptRecord b, Receipt owner) {
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
                .owner(owner)
                .createdList(new ArrayList<>())
                .build();
    }

    private void cancelReceiptRecord(ReceiptRecord receiptRecord, Receipt owner) {
        owner.getRecords().remove(receiptRecord);
        receiptRecordRepository.delete(receiptRecord);
    }
}
