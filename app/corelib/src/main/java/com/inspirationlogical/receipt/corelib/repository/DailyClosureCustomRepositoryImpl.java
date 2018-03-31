package com.inspirationlogical.receipt.corelib.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DailyClosureCustomRepositoryImpl implements DailyClosureCustomRepository {

    @Autowired
    private DailyClosureRepository dailyClosureRepository;

    @Override
    public List<LocalDateTime> getClosureTimes(LocalDate startTime, LocalDate endTime) {
        List<LocalDateTime> closureTimes = new ArrayList<>();
        closureTimes.add(dailyClosureRepository.getDailyClosureBeforeDate(startDate.atTime(5, 0)));
        return null;
    }

    public static List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate) {
        List<LocalDateTime> closureTimes = new ArrayList<>();
        closureTimes.add(getClosureTimeBeforeDate(startDate.atTime(5, 0)));
        closureTimes.add(getClosureTimeAfterDate(endDate.atTime(21, 0)));
        return closureTimes;
    }
}
