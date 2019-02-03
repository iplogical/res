package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import lombok.Builder;
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
    private int totalConsumption;

    private int productDiscount;
    private int tableDiscount;
    private int totalDiscount;
}
