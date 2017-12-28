package com.inspirationlogical.receipt.manager.controller.receipt;

import lombok.Data;

@Data
public class SummaryValues {

    private int numberOfReceipts;
    private int cashNetIncome;
    private int cashGrossIncome;
    private int creditCardNetIncome;
    private int creditCardGrossIncome;
    private int couponNetIncome;
    private int couponGrossIncome;
    private int totalNetIncome;
    private int totalGrossIncome;

    private int totalGrossExpenditure;
}
