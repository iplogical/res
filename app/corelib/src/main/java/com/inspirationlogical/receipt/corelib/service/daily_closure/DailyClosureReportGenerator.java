package com.inspirationlogical.receipt.corelib.service.daily_closure;

import java.time.LocalDate;

public interface DailyClosureReportGenerator {

    String createDailyClosureReport(LocalDate startDate, LocalDate endDate);

}
