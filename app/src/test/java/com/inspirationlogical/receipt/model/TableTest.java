package com.inspirationlogical.receipt.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

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

    private void assertListSize() {
        assertEquals(2, persistTebleAndGetList().size());
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
        manager.persist(schema.getTableOne());
        manager.persist(schema.getTableTwo());
        manager.getTransaction().commit();
    }
}
