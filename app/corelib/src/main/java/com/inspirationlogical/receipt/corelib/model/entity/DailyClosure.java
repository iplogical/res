package com.inspirationlogical.receipt.corelib.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "DAILY_CLOSURE")
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@AttributeOverride(name = "id", column = @Column(name = "DAILY_CLOSURE_ID"))
public @Data
class DailyClosure extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    private LocalDateTime closureTime;

    private int sumPurchaseNetPriceCash;
    private int sumPurchaseNetPriceCreditCard;
    private int sumPurchaseNetPriceCoupon;
    private int sumPurchaseNetPriceTotal;

    private int sumPurchaseGrossPriceCash;
    private int sumPurchaseGrossPriceCreditCard;
    private int sumPurchaseGrossPriceCoupon;
    private int sumPurchaseGrossPriceTotal;

    private int sumSaleNetPriceCash;
    private int sumSaleNetPriceCreditCard;
    private int sumSaleNetPriceCoupon;
    private int sumSaleNetPriceTotal;

    private int sumSaleGrossPriceCash;
    private int sumSaleGrossPriceCreditCard;
    private int sumSaleGrossPriceCoupon;
    private int sumSaleGrossPriceTotal;

    private int profit;

    private double markup;

    int receiptAverage;

    int numberOfReceipts;

    double discount;

    @Tolerate
    DailyClosure() {
    }
}
