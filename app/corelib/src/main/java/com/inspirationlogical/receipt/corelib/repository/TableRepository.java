package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TableRepository extends JpaRepository<Table, Integer> {

    List<Table> findAllByVisibleIsTrue();

    Table findByNumber(int number);

    List<Table> findAllByType(TableType type);

    @Query("SELECT max(t.number) + 1 FROM Table t WHERE (t.type = 'NORMAL' OR t.type = 'LOITERER' OR t.type = 'FREQUENTER' OR t.type = 'EMPLOYEE')")
    int getFirstUnusedNumber();
}
