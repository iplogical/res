package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptRecordRepository extends JpaRepository<ReceiptRecord, Long> {

    @Query("FROM ReceiptRecord r INNER JOIN r.createdList cl WHERE cl.created >:created AND r.name=:name ORDER BY cl.created DESC")
    List<ReceiptRecord> getReceiptRecordByTimestamp(@Param(value = "name") String name, @Param(value = "created") LocalDateTime created);
}
