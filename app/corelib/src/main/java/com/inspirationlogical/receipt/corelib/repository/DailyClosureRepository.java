package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DailyClosureRepository extends JpaRepository<DailyClosure, Long>, DailyClosureCustomRepository {

    DailyClosure findByClosureTimeIsNull();

    @Query("FROM DailyClosure dc WHERE dc.closureTime < :closureTime ORDER BY dc.closureTime desc")
    DailyClosure getDailyClosureBeforeDate(@Param(value = "closureTime")LocalDateTime closureTime);
}
