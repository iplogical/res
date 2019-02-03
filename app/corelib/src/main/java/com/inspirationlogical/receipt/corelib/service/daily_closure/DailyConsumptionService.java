package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;

import java.time.LocalDate;

public interface DailyConsumptionService {

    void printAggregatedConsumption(DailyConsumptionModel dailyConsumptionModel);

    DailyConsumptionModel getDailyConsumptionModel(LocalDate startTime, LocalDate endTime);
}
