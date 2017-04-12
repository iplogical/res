package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RESERVATIONS;
import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_TABLES;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class TableTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

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
