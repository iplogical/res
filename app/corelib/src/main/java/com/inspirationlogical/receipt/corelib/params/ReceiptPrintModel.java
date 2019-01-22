package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Getter;
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

    private String receiptType;
    private int totalPrice;
    private int totalDiscount;
    private int discountedTotalPrice;
    private int roundedTotalPrice;
    private String paymentMethod;

    private String receiptDisclaimer;
    private String receiptNote;
    private String receiptGreet;
    private LocalDateTime closureTime;
    private long receiptId;
}
