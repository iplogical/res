package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class CloseDayParams {

    int totalCommerce;
    int otherIncome;
    int creditCardTerminal;
    int serviceFeeOver;
    LocalDate startDate;
    LocalDate endDate;
}
