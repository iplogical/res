package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosureNew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyClosureNewRepository extends JpaRepository<DailyClosureNew, Integer> {

    List<DailyClosureNew> findAllByClosureTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
