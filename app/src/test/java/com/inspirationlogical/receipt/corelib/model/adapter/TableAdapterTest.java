package com.inspirationlogical.receipt.corelib.model.adapter;

import static org.junit.Assert.*;

import com.inspirationlogical.receipt.corelib.exception.TableAlreadyOpenException;
import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;

public class TableAdapterTest {

    TableAdapter tableAdapter;
    TableAdapter closedTableAdapter;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        tableAdapter = new TableAdapter(schema.getTableNormal(), schema.getEntityManager());
        closedTableAdapter = new TableAdapter(schema.getTableNormalClosed(), schema.getEntityManager());
    }

    @Test
    public void testNormalTableHasAnActiveReceipt() {
        assertNotNull(tableAdapter.getActiveReceipt());
    }

    @Test
    public void testSetTableName() {
        tableAdapter.setTableName("New Table Name");
        assertEquals("New Table Name", TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getName());
    }

    @Test
    public void testSetCapacity() {
        tableAdapter.setCapacity(10);
        assertEquals(10, TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getCapacity());
    }

    @Test
    public void testSetNote() {
        tableAdapter.setNote("Big chocklate cake for Spicces Feri");
        assertEquals("Big chocklate cake for Spicces Feri",
                TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getNote());
    }

    @Test
    public void testDisplayTable() {
        tableAdapter.displayTable();
        assertTrue(TableAdapter.getTableByNumber(schema.getEntityManager(),
                        tableAdapter.getAdaptee().getNumber()).getAdaptee().isVisibility());
    }

    @Test
    public void testHideTable() {
        tableAdapter.hideTable();
        assertFalse(TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().isVisibility());
    }

    @Test
    public void testMoveTable() {
        tableAdapter.moveTable(new Point2D(50, 70));
        assertEquals(50, TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getCoordinateX());
        assertEquals(70, TableAdapter.getTableByNumber(schema.getEntityManager(),
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