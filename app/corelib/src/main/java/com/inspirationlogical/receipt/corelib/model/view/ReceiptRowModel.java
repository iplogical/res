package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class ReceiptRowModel {

    private String receiptId;
    private String receiptTotalPrice;
    private String receiptPaymentMethod;
    private String receiptOpenTime;
    private String receiptClosureTime;
}
