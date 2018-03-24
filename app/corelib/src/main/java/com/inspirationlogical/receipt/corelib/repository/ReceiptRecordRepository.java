package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRecordRepository extends JpaRepository<ReceiptRecord, Long> {
}
