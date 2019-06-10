package com.inspirationlogical.receipt.corelib.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "DAILY_CLOSURE_NEW")
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "DAILY_CLOSURE_NEW_ID"))
public class DailyClosureNew extends AbstractEntity {

    @Column(name = "CLOSURE_TIME")
    private LocalDateTime closureTime;

    @Column(name = "TOTAL_CASH")
    private int totalCash;

    @Column(name = "TOTAL_CREDIT_CARD")
    private int totalCreditCard;

    @Column(name = "TOTAL_COUPON")
    private int totalCoupon;

    @Column(name = "SERVICE_FEE_CASH")
    private int serviceFeeCash;

    @Column(name = "SERVICE_FEE_CREDIT_CARD")
    private int serviceFeeCreditCard;

    @Column(name = "SERVICE_FEE_COUPON")
    private int serviceFeeCoupon;

    @Column(name = "SERVICE_FEE_NET")
    private int serviceFeeNet;

    @Column(name = "SERVICE_FEE_TOTAL")
    private int serviceFeeTotal;

    @Column(name = "TOTAL_Commerce")
    private int totalCommerce;

    @Column(name = "OTHER_INCOME")
    private int otherIncome;

    @Column(name = "CREDIT_CARD_TERMINAL")
    private int creditCardTerminal;

    @Column(name = "SERVICE_FEE_OVER")
    private int serviceFeeOver;
}
