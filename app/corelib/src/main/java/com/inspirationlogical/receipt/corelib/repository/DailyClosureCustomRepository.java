package com.inspirationlogical.receipt.corelib.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DailyClosureCustomRepository {

    List<LocalDateTime> getClosureTimes(LocalDate startTime, LocalDate endTime)
}
