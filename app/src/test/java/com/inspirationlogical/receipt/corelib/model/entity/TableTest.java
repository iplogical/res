package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TableTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testTableCreation() {
        assertEquals(7, getTables().size());
    }

    @Test
    public void testReservationNumber() {
        List<Table> tables = getTables().stream()
                .filter(table -> table.getNumber()==1)
                .collect(Collectors.toList());
        assertEquals(1, tables.size());
        assertEquals(2, tables.get(0).getReservation().size());
    }

    @Test(expected = RollbackException.class)
    public void notUniqueTableNumber() {
        GuardedTransaction.Run(()->
                schema.getTableVirtual().setNumber(schema.getTableNormal().getNumber()));
    }

    @Test(expected = RollbackException.class)
    public void TableWithoutNumber() {
        GuardedTransaction.Run(()->
                schema.getTableVirtual().setNumber(0));
    }

    @Test(expected = RollbackException.class)
    public void tableWithoutOwner() {
        GuardedTransaction.Run(()->
                schema.getTableOther().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void tableWithoutType() {
        GuardedTransaction.Run(()->
                schema.getTableOther().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void tableWithMoreOpenReceipts() {
        GuardedTransaction.Run(()->
                schema.getReceiptSaleTwo().setStatus(ReceiptStatus.OPEN));
    }

    private List<Table> getTables() {
        @SuppressWarnings("unchecked")
        List<Table> entries = schema.getEntityManager().createNamedQuery(Table.GET_TEST_TABLES).getResultList();
        return entries;
    }
}
