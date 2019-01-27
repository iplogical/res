package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.VAT;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VATRepository extends JpaRepository<VAT, Integer> {

    @Query("FROM VAT v WHERE v.name = :name AND v.status = 'VALID'")
    VAT getVatByName(@Param(value = "name") VATName name);
}
