package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordCreated;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRecordCreatedRepository extends JpaRepository<ReceiptRecordCreated, Long> {
}
