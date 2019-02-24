package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRowModel;

import java.time.LocalDate;
import java.util.List;

public interface DailyConsumptionService {

    void printAggregatedConsumption(DailyConsumptionModel dailyConsumptionModel);

    DailyConsumptionModel getDailyConsumptionModel(LocalDate startTime, LocalDate endTime);

    List<ReceiptRowModel> getReceipts(LocalDate startDate, LocalDate endDate);
}
