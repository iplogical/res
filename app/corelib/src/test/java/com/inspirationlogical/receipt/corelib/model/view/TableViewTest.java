package com.inspirationlogical.receipt.corelib.model.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.inspirationlogical.receipt.corelib.model.TestBase;

import org.junit.Before;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import javafx.geometry.Point2D;

/**
 * Created by Bálint on 2017.03.13..
 */
public class TableViewTest extends TestBase {
    private TableView tableView;

    @Before
    public void createTableView() {
        tableView = new TableView(new TableAdapter(schema.getTableNormal()));
    }

    @Test
    public void testIsOpen() {
        assertTrue(tableView.isOpen());
    }

    @Test
    public void testIsVisible() {
        assertTrue(tableView.isVisible());
    }

    @Test
    public void testGetType() {
        assertEquals(TableType.NORMAL, tableView.getType());
    }

    @Test
    public void testGetTableNumber() {
        assertEquals(1, tableView.getNumber());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(6, tableView.getCapacity());
    }

    @Test
    public void testGetGuestCount() {
        assertEquals(4, tableView.getGuestCount());
    }

    @Test
    public void testGetName() {
        assertEquals("Spicces Feri", tableView.getName());
    }

    @Test
    public void testGetCoordinates() {
        assertEquals(new Point2D(100,50), tableView.getPosition());
    }
}
