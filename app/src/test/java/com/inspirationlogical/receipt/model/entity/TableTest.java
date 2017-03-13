package com.inspirationlogical.receipt.model.entity;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import com.inspirationlogical.receipt.model.entity.Table;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.testsuite.ModelTest;

@Category(ModelTest.class)
public class TableTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testTableCreation() {
        assertListSize();
    }

    @Test
    public void testReservationNumber() {
        List<Table> tables = persistTebleAndGetList();
        for(Table t : tables) {
            if(t.getType() == TableType.NORMAL) {
                assertEquals(2, t.getReservation().size());
            }
        }
    }

    @Test(expected = RollbackException.class)
    public void notUniqueTableNumber() {
        schema.getTableVirtual().setNumber(schema.getTableNormal().getNumber());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void TableWithoutNumber() {
        schema.getTableVirtual().setNumber(0);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void tableWithoutOwner() {
        schema.getTableOther().setOwner(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void tableWithoutType() {
        schema.getTableOther().setType(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void tableWithMoreOpenReceipts() {
        schema.getReceiptSaleTwo().setStatus(ReceiptStatus.OPEN);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(6, persistTebleAndGetList().size());
    }

    private List<Table> persistTebleAndGetList() {
        persistTable();
        @SuppressWarnings("unchecked")
        List<Table> entries = manager.createNamedQuery(Table.GET_TEST_TABLES).getResultList();
        return entries;
    }

    private void persistTable() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getTableNormal());
        manager.persist(schema.getTableVirtual());
        manager.getTransaction().commit();
    }
}
