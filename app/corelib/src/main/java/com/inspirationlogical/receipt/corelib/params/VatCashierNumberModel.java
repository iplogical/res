package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class VatCashierNumberModel {

    private int vatDrinkCashierNumber;
    private int vatDrinkServiceFeeCashierNumber;
    private int vatFoodCashierNumber;
    private int vatFoodServiceFeeCashierNumber;
}
