package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRowModel;
import com.inspirationlogical.receipt.corelib.params.CloseDayParams;

import java.time.LocalDate;
import java.util.List;

public interface DailyConsumptionService {

    void printAggregatedConsumption(DailyConsumptionModel dailyConsumptionModel);

    DailyConsumptionModel getDailyConsumptionModel(LocalDate startTime, LocalDate endTime);

    List<ReceiptRowModel> getReceipts(LocalDate startDate, LocalDate endDate);

    void updatePaymentMethod(int receiptId, PaymentMethod paymentMethod);

    void closeDay(CloseDayParams closeDayParams);
}
