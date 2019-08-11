package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class VatPriceModel {

    double vatPercent;
    int price;
    int serviceFee;
    int totalPrice;
}
