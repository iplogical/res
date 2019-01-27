package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VATSerieRepository extends JpaRepository<VATSerie, Integer> {

    VATSerie findFirstByStatus(VATStatus status);
}
