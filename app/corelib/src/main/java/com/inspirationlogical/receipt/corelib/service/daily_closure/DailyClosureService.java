package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DailyClosureService {

    List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate);

    void closeDay();

    void update(Receipt receipt);
}
