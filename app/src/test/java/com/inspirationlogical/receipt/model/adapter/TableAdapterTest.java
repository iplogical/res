package com.inspirationlogical.receipt.model.adapter;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.exception.TableAlreadyOpenException;
import com.inspirationlogical.receipt.model.TestType;
import com.inspirationlogical.receipt.view.DatabaseCreator;
import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;

public class TableAdapterTest {

    private EntityManager manager;
    TableAdapter tableAdapter;
    TableAdapter closedTableAdapter;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getRestaurant());
        manager.getTransaction().commit();
        tableAdapter = new TableAdapter(schema.getTableNormal(), manager);
        closedTableAdapter = new TableAdapter(schema.getTableNormalClosed(), manager);
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

    @Test
    public void testSetCapacity() {
        tableAdapter.setCapacity(10);
        assertEquals(10, TableAdapter.getTableByNumber(manager,
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getCapacity());
    }

    @Test
    public void testSetNote() {
        tableAdapter.setNote("Big chocklate cake for Spicces Feri");
        assertEquals("Big chocklate cake for Spicces Feri",
                TableAdapter.getTableByNumber(manager,
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getNote());
    }

    @Test
    public void testDisplayTable() {
        tableAdapter.displayTable();
        assertTrue(TableAdapter.getTableByNumber(manager,
                        tableAdapter.getAdaptee().getNumber()).getAdaptee().isVisibility());
    }

    @Test
    public void testHideTable() {
        tableAdapter.hideTable();
        assertFalse(TableAdapter.getTableByNumber(manager,
                tableAdapter.getAdaptee().getNumber()).getAdaptee().isVisibility());
    }

    @Test
    public void testMoveTable() {
        tableAdapter.moveTable(new Point2D(50, 70));
        assertEquals(50, TableAdapter.getTableByNumber(manager,
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getCoordinateX());
        assertEquals(70, TableAdapter.getTableByNumber(manager,
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getCoordinateY());
    }

    @Test(expected = TableAlreadyOpenException.class)
    public void testOpenTableAlreadyOpen() {
        tableAdapter.openTable();
    }

    @Test
    public void testOpenTable() {
        closedTableAdapter.openTable();
        assertNotNull(closedTableAdapter.getActiveReceipt());
    }
}
