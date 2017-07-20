package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidReceipts;
import lombok.experimental.Tolerate;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
@javax.persistence.Table(name = "RECEIPT_HISTORICAL")
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_HISTORICAL_ID"))

@ValidReceipts
public class ReceiptHistorical extends Receipt {

    public ReceiptHistorical(Receipt receipt) {
        this.setOwner(receipt.getOwner());
//        this.setRecords(receipt.getRecords());
        this.setRecords(new ArrayList<>());
        this.setVATSerie(receipt.getVATSerie());
        this.setType(receipt.getType());
        this.setStatus(receipt.getStatus());
        this.setPaymentMethod(receipt.getPaymentMethod());
        this.setOpenTime(receipt.getOpenTime());
        this.setClosureTime(receipt.getClosureTime());
        this.setUserCode(receipt.getUserCode());
        this.setSumPurchaseNetPrice(receipt.getSumPurchaseNetPrice());
        this.setSumPurchaseGrossPrice(receipt.getSumPurchaseGrossPrice());
        this.setSumSaleNetPrice(receipt.getSumSaleNetPrice());
        this.setSumSaleGrossPrice(receipt.getSumSaleGrossPrice());
        this.setDiscountPercent(receipt.getDiscountPercent());
    }

    @Tolerate
    ReceiptHistorical(){}

}
