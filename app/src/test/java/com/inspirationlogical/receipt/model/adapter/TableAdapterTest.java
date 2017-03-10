package com.inspirationlogical.receipt.model.adapter;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;

public class TableAdapterTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getTableNormal());
        manager.getTransaction().commit();
    }

    @Test
    public void testNormalTableHasAnActiveReceipt() {
        TableAdapter tableAdapter = new TableAdapter(schema.getTableNormal(), manager);
        assertNotNull(tableAdapter.getActiveReceipt());
    }
}
