package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_RESERVATIONS;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_TABLES;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;

public class TableTest extends TestBase {

    @Test
    public void testTableCreation() {
        assertEquals(NUMBER_OF_TABLES, getTables().size());
    }

    @Test
    public void testReservationNumber() {
        List<Table> tables = getTables().stream()
                .filter(table -> table.getNumber()==1)
                .collect(Collectors.toList());
        assertEquals(1, tables.size());
        assertEquals(NUMBER_OF_RESERVATIONS, tables.get(0).getReservations().size());
    }

    @Test(expected = RollbackException.class)
    public void notUniqueTableNumber() {
        GuardedTransaction.run(()->
                schema.getTableLoiterer().setNumber(schema.getTableNormal().getNumber()));
    }

    @Test(expected = RollbackException.class)
    public void TableWithoutNumber() {
        GuardedTransaction.run(()->
                schema.getTableLoiterer().setNumber(-1));
    }

    @Test(expected = RollbackException.class)
    public void tableWithoutOwner() {
        GuardedTransaction.run(()->
                schema.getTableOther().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void tableWithoutType() {
        GuardedTransaction.run(()->
                schema.getTableOther().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void tableWithMoreOpenReceipts() {
        GuardedTransaction.run(()->
                schema.getReceiptSaleTwo().setStatus(ReceiptStatus.OPEN));
    }

    private List<Table> getTables() {
        @SuppressWarnings("unchecked")
        List<Table> entries = schema.getEntityManager().createNamedQuery(Table.GET_ALL_TABLES).getResultList();
        return entries;
    }
}
