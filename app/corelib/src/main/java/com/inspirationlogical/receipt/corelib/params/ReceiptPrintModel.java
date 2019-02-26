package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
public class ReceiptPrintModel {

    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPhoneNumber;
    private String restaurantSocialMediaInfo;
    private String restaurantWebsite;

    private List<ReceiptRecordPrintModel> receiptRecordsPrintModels;

    @Setter
    private int totalPriceNoServiceFee;
    @Setter
    private int serviceFee;
    @Setter
    private int serviceFeePercent;
    private int totalPriceWithServiceFee;
    private int totalDiscount;
    private int discountedTotalPrice;
    private int roundedTotalPrice;

    private int consumptionCash;
    private int consumptionCreditCard;
    private int consumptionCoupon;
    private int openConsumption;
    private int netServiceFee;
    private int totalConsumption;
    private int productDiscount;
    private int tableDiscount;

    @Setter
    private String paymentMethod;

    private String receiptDisclaimer;
    @Setter
    private String receiptNote;
    private String receiptGreet;

    @Setter
    private LocalDateTime closureTime;
    private long receiptId;
}
