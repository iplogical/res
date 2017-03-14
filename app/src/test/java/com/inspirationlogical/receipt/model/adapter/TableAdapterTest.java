package com.inspirationlogical.receipt.model.adapter;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.TestType;
import com.inspirationlogical.receipt.view.DatabaseCreator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;

public class TableAdapterTest {

    private EntityManager manager;
    TableAdapter tableAdapter;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule(TestType.VALIDATE);

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        tableAdapter = new TableAdapter(schema.getTableNormal(), manager);
    }

    @Test
    public void testNormalTableHasAnActiveReceipt() {
        assertNotNull(tableAdapter.getActiveReceipt());
    }

    @Test
    public void testSetTableName() {
        tableAdapter.setTableName("New Table Name");
        assertEquals("New Table Name", TableAdapter.getTableByNumber(manager,
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getName());
    }
}
