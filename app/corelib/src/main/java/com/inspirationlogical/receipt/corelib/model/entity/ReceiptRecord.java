package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "product", "createdList"})
@Table(name = "RECEIPT_RECORD")
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_RECORD_ID"))
public @Data
class ReceiptRecord extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RECEIPT_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Receipt owner;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ReceiptRecordCreated> createdList;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private ReceiptRecordType type;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SOLDQUANTITY")
    private double soldQuantity;

    @Column(name = "ABSOLUTEQUANTITY")
    private double absoluteQuantity;

    @Column(name = "PURCHASEPRICE")
    private int purchasePrice;

    @Column(name = "SALEPRICE")
    private int salePrice;

    @Column(name = "ORIGINALSALEPRICE")
    private int originalSalePrice;

    @ManyToOne
    @JoinColumn(name = "VAT")
    private VAT VAT;

    @Column(name = "DISCOUNTPERCENT")
    private double discountPercent;

    @Tolerate
    ReceiptRecord() {
    }

    public static ReceiptRecord cloneReceiptRecord(ReceiptRecord record) {
        ReceiptRecord newRecord = new ReceiptRecord();
        newRecord.setType(record.getType());
        newRecord.setName(record.getName());
        newRecord.setSoldQuantity(record.getSoldQuantity());
        newRecord.setAbsoluteQuantity(record.getAbsoluteQuantity());
        newRecord.setPurchasePrice(record.getPurchasePrice());
        newRecord.setSalePrice(record.getSalePrice());
        newRecord.setOriginalSalePrice(record.getOriginalSalePrice());
        newRecord.setVAT(record.getVAT());
        newRecord.setDiscountPercent(record.getDiscountPercent());
        newRecord.setCreatedList(new ArrayList<>());
        return newRecord;
    }

    @Override
    public String toString() {
        return "ReceiptRecord{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", soldQuantity=" + soldQuantity +
                ", absoluteQuantity=" + absoluteQuantity +
                ", purchasePrice=" + purchasePrice +
                ", salePrice=" + salePrice +
                ", VAT=" + VAT +
                ", discountPercent=" + discountPercent +
                '}';
    }
}
