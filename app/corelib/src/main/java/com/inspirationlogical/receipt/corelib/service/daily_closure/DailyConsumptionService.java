package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;

import java.time.LocalDate;

public interface DailyConsumptionService {
    void printAggregatedConsumption(LocalDate startTime, LocalDate endTime);

    ReceiptView getAggregatedReceipt(LocalDate startTime, LocalDate endTime);
}
