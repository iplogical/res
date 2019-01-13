package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;

import java.time.LocalDate;
import java.util.Map;

public interface DailyConsumptionService {

    Map<PaymentMethod, Integer> getConsumptionOfTheDay();

    int getOpenConsumption();

    void printAggregatedConsumption(LocalDate startTime, LocalDate endTime);

    ReceiptView getAggregatedReceipt(LocalDate startTime, LocalDate endTime);
}
