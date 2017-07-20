package com.inspirationlogical.receipt.corelib.model.entity;

import lombok.experimental.Tolerate;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
@javax.persistence.Table(name = "RECEIPT_RECORD_HISTORICAL")
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_RECORD_HISTORICAL_ID"))
public class ReceiptRecordHistorical extends ReceiptRecord {

    public ReceiptRecordHistorical(ReceiptRecord record, Receipt owner){
        this.setOwner(owner);
        this.setProduct(record.getProduct());
        this.setType(record.getType());
        this.setName(record.getName());
        this.setSoldQuantity(record.getSoldQuantity());
        this.setAbsoluteQuantity(record.getAbsoluteQuantity());
        this.setPurchasePrice(record.getPurchasePrice());
        this.setSalePrice(record.getSalePrice());
        this.setVAT(record.getVAT());
        this.setDiscountPercent(record.getDiscountPercent());
        this.setCreatedList(new ArrayList<>());
    }

    @Tolerate
    ReceiptRecordHistorical(){}
}
