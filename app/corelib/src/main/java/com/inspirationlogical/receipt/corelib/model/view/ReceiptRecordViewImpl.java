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

/**
 * Created by Bálint on 2017.03.15..
 */
@Getter
@ToString
public class ReceiptRecordViewImpl implements ReceiptRecordView {

    private long id;
    private String name;
    private ReceiptRecordType type;
    private double soldQuantity;
    private double absoluteQuantity;
    private int purchasePrice;
    private int salePrice;
    private int totalPrice;
    private double discountPercent;
    private double vat;
    private List<LocalDateTime> created;
    private boolean isPartiallyPayable;
    private ReceiptStatus ownerStatus;


    public ReceiptRecordViewImpl(ReceiptRecord receiptRecord) {
        id = receiptRecord.getId();
        name = receiptRecord.getName();
        type = receiptRecord.getType();
        soldQuantity = receiptRecord.getSoldQuantity();
        absoluteQuantity = receiptRecord.getAbsoluteQuantity();
        purchasePrice = receiptRecord.getPurchasePrice();
        salePrice = receiptRecord.getSalePrice();
        totalPrice = (int)Math.round(receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity());
        discountPercent = receiptRecord.getDiscountPercent();
        vat = receiptRecord.getVAT();
        created = initCreated(receiptRecord);
        isPartiallyPayable = receiptRecord.getProduct().getType().equals(ProductType.PARTIALLY_PAYABLE);
        ownerStatus = receiptRecord.getOwner().getStatus();
    }

    private List<LocalDateTime> initCreated(ReceiptRecord receiptRecord) {
        return receiptRecord.getCreatedList().stream()
                .map(ReceiptRecordCreated::getCreated)
                .collect(toList());
    }
}
