package com.inspirationlogical.receipt.corelib.params;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class VatPriceModel {

    double vatPercent;
    int price;
    int serviceFee;
    int totalPrice;
}
