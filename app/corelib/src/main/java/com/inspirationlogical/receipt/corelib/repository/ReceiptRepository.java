package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Receipt findById(long id);

    @Query("SELECT r FROM Receipt r WHERE r.status='OPEN' AND r.owner.number=:number")
    Receipt getOpenReceipt(@Param(value = "number") Integer number);

    @Query("SELECT r FROM Receipt r WHERE r.status=:status AND r.owner.number=:number")
    List<Receipt> getReceiptByStatusAndOwner(@Param(value = "status")ReceiptStatus status, @Param(value = "number") Integer number);

    @Query("SELECT r FROM Receipt r WHERE r.closureTime>:startTime AND r.closureTime<:endTime AND r.type='SALE'")
    List<Receipt> getReceiptsByClosureTime(@Param(value = "startTime")LocalDateTime startTime, @Param(value = "endTime")LocalDateTime endTime);
}
