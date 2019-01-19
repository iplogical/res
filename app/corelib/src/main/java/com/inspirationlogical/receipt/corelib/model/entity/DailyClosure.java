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

    @Column(name = "CLOSURETIME")
    private LocalDateTime closureTime;

    @Column(name = "SUMPURCHASENETPRICECASH")
    private int sumPurchaseNetPriceCash;

    @Column(name = "SUMPURCHASENETPRICECREDITCARD")
    private int sumPurchaseNetPriceCreditCard;

    @Column(name = "SUMPURCHASENETPRICECOUPON")
    private int sumPurchaseNetPriceCoupon;

    @Column(name = "SUMPURCHASENETPRICETOTAL")
    private int sumPurchaseNetPriceTotal;

    @Column(name = "SUMPURCHASEGROSSPRICECASH")
    private int sumPurchaseGrossPriceCash;

    @Column(name = "SUMPURCHASEGROSSPRICECREDITCARD")
    private int sumPurchaseGrossPriceCreditCard;

    @Column(name = "SUMPURCHASEGROSSPRICECOUPON")
    private int sumPurchaseGrossPriceCoupon;

    @Column(name = "SUMPURCHASEGROSSPRICETOTAL")
    private int sumPurchaseGrossPriceTotal;

    @Column(name = "SUMSALENETPRICECASH")
    private int sumSaleNetPriceCash;

    @Column(name = "SUMSALENETPRICECREDITCARD")
    private int sumSaleNetPriceCreditCard;

    @Column(name = "SUMSALENETPRICECOUPON")
    private int sumSaleNetPriceCoupon;

    @Column(name = "SUMSALENETPRICETOTAL")
    private int sumSaleNetPriceTotal;

    @Column(name = "SUMSALEGROSSPRICECASH")
    private int sumSaleGrossPriceCash;

    @Column(name = "SUMSALEGROSSPRICECREDITCARD")
    private int sumSaleGrossPriceCreditCard;

    @Column(name = "SUMSALEGROSSPRICECOUPON")
    private int sumSaleGrossPriceCoupon;

    @Column(name = "SUMSALEGROSSPRICETOTAL")
    private int sumSaleGrossPriceTotal;

    @Column(name = "PROFIT")
    private int profit;

    @Column(name = "MARKUP")
    private double markup;

    @Column(name = "RECEIPTAVERAGE")
    int receiptAverage;

    @Column(name = "NUMBEROFRECEIPTS")
    int numberOfReceipts;

    @Column(name = "DISCOUNT")
    double discount;

    @Tolerate
    DailyClosure() {
    }
}
