package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryFamily;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@ToString
@Builder
public class ReceiptRecordView {

    private int id;
    private String name;
    private int productId;
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
    private ProductCategoryFamily family;

    @Tolerate
    public ReceiptRecordView() {
    }

    @Tolerate
    public ReceiptRecordView(ReceiptRecord receiptRecord) {
        id = receiptRecord.getId();
        name = receiptRecord.getName();
        productId = receiptRecord.getProduct().getId();
        type = receiptRecord.getType();
        soldQuantity = receiptRecord.getSoldQuantity();
        purchasePrice = receiptRecord.getPurchasePrice();
        salePrice = receiptRecord.getSalePrice();
        totalPrice = (int)Math.round(receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity());
        discountPercent = receiptRecord.getDiscountPercent();
        vat = receiptRecord.getVAT().getVAT();
        created = initCreated(receiptRecord);
        isPartiallyPayable = receiptRecord.getProduct() != null && receiptRecord.getProduct().getType().equals(ProductType.PARTIALLY_PAYABLE);
        ownerStatus = receiptRecord.getOwner().getStatus();
        if (isNormalReceiptRecord(receiptRecord)) {
            family = ProductCategoryFamily.initFamily(receiptRecord.getProduct());
        } else {
            family = ProductCategoryFamily.FOOD;    // TODO: Fix this. Due to aggregated receipt special records
        }
    }

    private boolean isNormalReceiptRecord(ReceiptRecord receiptRecord) {
        return receiptRecord.getProduct() != null;
    }

    private List<LocalDateTime> initCreated(ReceiptRecord receiptRecord) {
        return receiptRecord.getCreatedList().stream()
                .map(ReceiptRecordCreated::getCreated)
                .collect(toList());
    }
}
