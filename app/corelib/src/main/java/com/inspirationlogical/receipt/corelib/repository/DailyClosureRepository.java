package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface DailyClosureRepository extends JpaRepository<DailyClosure, Integer> {

    DailyClosure findByClosureTimeIsNull();

    Optional<DailyClosure> findTopByClosureTimeBeforeOrderByClosureTimeDesc(LocalDateTime closureTime);

    Optional<DailyClosure> findTopByClosureTimeAfterOrderByClosureTimeAsc(LocalDateTime closureTime);
}
