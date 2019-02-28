package com.inspirationlogical.receipt.corelib.model.view;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DailyConsumptionModel {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<ReceiptRecordView> soldProducts;
    private int openConsumption;
    private int consumptionCash;
    private int consumptionCreditCard;
    private int consumptionCoupon;
    private int serviceFeeCash;
    private int serviceFeeNetCash;
    private int serviceFeeCreditCard;
    private int serviceFeeNetCreditCard;
    private int serviceFeeCoupon;
    private int serviceFeeTotal;
    private int serviceFeeNetTotal;
    private int totalConsumption;

    private int productDiscount;
    private int tableDiscount;
    private int totalDiscount;
}
