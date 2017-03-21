package com.inspirationlogical.receipt.corelib.model.adapter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;
import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.RollbackException;
import java.util.ArrayList;
import java.util.Collection;

public class TableAdapterTest {

    TableAdapter tableAdapter;
    TableAdapter closedTableAdapter;
    PaymentParams paymentParams;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void persistObjects() {
        tableAdapter = new TableAdapter(schema.getTableNormal());
        closedTableAdapter = new TableAdapter(schema.getTableNormalClosed());
        paymentParams = PaymentParams.builder()
                .paymentMethod(PaymentMethod.COUPON)
                .userCode(1000)
                .discountAbsolute(0)
                .discountPercent(0D)
                .build();
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
    public void testSetTableNumber() {
        tableAdapter.setTableNumber(8);
        assertEquals(8, TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getNumber());
    }

    @Test(expected = RollbackException.class)
    public void testSetTableNumberAlreadyUsed() {
        tableAdapter.setTableNumber(3);
        assertEquals(3, TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getNumber());
    }

    @Test
    public void testSetTableType() {
        tableAdapter.setTableType(TableType.VIRTUAL);
        assertEquals(TableType.VIRTUAL, TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getType());
    }

    @Test
    public void testSetCapacity() {
        tableAdapter.setCapacity(10);
        assertEquals(10, TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getCapacity());
    }

    @Test
    public void testSetGuestNumber() {
        tableAdapter.setGuestNumber(5);
        assertEquals(5, TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()).getAdaptee().getGuestNumber());
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

    @Test(expected = IllegalTableStateException.class)
    public void testOpenTableAlreadyOpen() {
        tableAdapter.openTable();
    }

    @Test
    public void testOpenTable() {
        closedTableAdapter.openTable();
        assertNotNull(closedTableAdapter.getActiveReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testOpenTableForOpen() {
        tableAdapter.openTable();
        assertNotNull(tableAdapter.getActiveReceipt());
    }

    @Test
    public void testPayTable() {
        tableAdapter.payTable(paymentParams);
        assertNull(tableAdapter.getActiveReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testPayTableWithClosedTable() {
        closedTableAdapter.payTable(paymentParams);
        assertNull(closedTableAdapter.getActiveReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testPaySelectiveWithClosedTable() {
        closedTableAdapter.paySelective(new ArrayList<>(), paymentParams);
        assertNull(closedTableAdapter.getActiveReceipt());
    }

    @Test
    public void testDeleteTable() {
        closedTableAdapter.deleteTable();
        assertNull(TableAdapter.getTableByNumber(schema.getEntityManager(),
                closedTableAdapter.getAdaptee().getNumber()));
    }

    @Test(expected = IllegalTableStateException.class)
    public void testDeleteTableWithOpenTable() {
        tableAdapter.deleteTable();
        assertNull(TableAdapter.getTableByNumber(schema.getEntityManager(),
                tableAdapter.getAdaptee().getNumber()));
    }
}
