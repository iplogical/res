package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceModifierRepository extends JpaRepository<PriceModifier, Long> {

    @Query("FROM PriceModifier pm WHERE pm.owner.id =:ownerId AND pm.startDate <:time AND pm.endDate >:time")
    List<PriceModifier> getPriceModifierByProductAndDates(
            @Param(value = "ownerId") long ownerId,
            @Param(value = "time")LocalDateTime time);
}
