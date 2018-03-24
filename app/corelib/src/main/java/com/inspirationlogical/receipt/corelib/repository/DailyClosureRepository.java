package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyClosureRepository extends JpaRepository<DailyClosure, Long> {

    DailyClosure findByClosureTimeIsNull();
}
