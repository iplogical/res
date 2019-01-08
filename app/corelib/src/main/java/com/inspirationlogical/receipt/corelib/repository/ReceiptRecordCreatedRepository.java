package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptRecordCreatedRepository extends JpaRepository<ReceiptRecordCreated, Long> {

    @Query("FROM ReceiptRecordCreated rrc WHERE rrc.created >:created AND rrc.owner.name=:name ORDER BY rrc.created DESC")
    List<ReceiptRecordCreated> findRecentByTimestamp(@Param(value = "name") String name, @Param(value = "created") LocalDateTime created);
}
