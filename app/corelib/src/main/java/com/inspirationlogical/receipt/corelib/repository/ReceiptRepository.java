package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Receipt findById(long id);

    @Query("SELECT r FROM Receipt r WHERE r.status='OPEN' AND r.owner.number=:number")
    Receipt getOpenReceipt(@Param(value = "number") Integer number);
}
