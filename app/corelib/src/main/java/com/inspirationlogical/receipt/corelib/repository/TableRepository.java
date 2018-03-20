package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableRepository extends JpaRepository<Table, Long> {

    List<Table> findAllByVisibleIsTrue();

    Table findByNumber(int number);

    List<Table> findAllByType(TableType type);

    List<Table> findAllByConsumer(Table consumer);

    List<Table> findAllByHost(Table host);
}
