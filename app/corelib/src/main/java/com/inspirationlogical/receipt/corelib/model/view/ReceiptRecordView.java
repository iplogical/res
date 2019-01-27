package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@ToString
public class ReceiptRecordView {

    private int id;
    private String name;
    private ReceiptRecordType type;
    private double soldQuantity;
    private int purchasePrice;
    private int salePrice;
    private int totalPrice;
    private double discountPercent;
    private double vat;
    private List<LocalDateTime> created;
    private boolean isPartiallyPayable;
    private ReceiptStatus ownerStatus;


    public ReceiptRecordView(ReceiptRecord receiptRecord) {
        id = receiptRecord.getId();
        name = receiptRecord.getName();
        type = receiptRecord.getType();
        soldQuantity = receiptRecord.getSoldQuantity();
        purchasePrice = receiptRecord.getPurchasePrice();
        salePrice = receiptRecord.getSalePrice();
        totalPrice = (int)Math.round(receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity());
        discountPercent = receiptRecord.getDiscountPercent();
        vat = receiptRecord.getVAT();
        created = initCreated(receiptRecord);
        isPartiallyPayable = receiptRecord.getProduct() != null && receiptRecord.getProduct().getType().equals(ProductType.PARTIALLY_PAYABLE);
        ownerStatus = receiptRecord.getOwner().getStatus();
    }

    private List<LocalDateTime> initCreated(ReceiptRecord receiptRecord) {
        return receiptRecord.getCreatedList().stream()
                .map(ReceiptRecordCreated::getCreated)
                .collect(toList());
    }
}
