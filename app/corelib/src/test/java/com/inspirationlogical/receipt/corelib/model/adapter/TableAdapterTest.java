package com.inspirationlogical.receipt.corelib.model.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.RollbackException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import javafx.geometry.Point2D;

public class TableAdapterTest {

    TableAdapter tableNormal;
    TableAdapter tableNormalClosed;
    TableAdapter tablePurchase;
    PaymentParams paymentParams;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        tableNormal = new TableAdapter(schema.getTableNormal());
        tableNormalClosed = new TableAdapter(schema.getTableNormalClosed());
        tablePurchase = new TableAdapter(schema.getTablePurchase());
        paymentParams = PaymentParams.builder()
                .paymentMethod(PaymentMethod.COUPON)
                .userCode(1000)
                .discountAbsolute(0)
                .discountPercent(0D)
                .build();
    }

    @Test
    public void testNormalTableHasAnActiveReceipt() {
        assertNotNull(tableNormal.getActiveReceipt());
    }

    @Test
    public void testSetTableName() {
        tableNormal.setName("New Table Name");
        assertEquals("New Table Name", TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getName());
    }

    @Test
    public void testSetTableNumber() {
        tableNormal.setNumber(8);
        assertEquals(8, TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getNumber());
    }

    @Test(expected = RollbackException.class)
    public void testSetTableNumberAlreadyUsed() {
        tableNormal.setNumber(3);
        assertEquals(3, TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getNumber());
    }

    @Test
    public void testSetTableType() {
        tableNormal.setType(TableType.LOITERER);
        assertEquals(TableType.LOITERER, TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getType());
    }

    @Test
    public void testSetCapacity() {
        tableNormal.setCapacity(10);
        assertEquals(10, TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCapacity());
    }

    @Test
    public void testSetGuestCount() {
        tableNormal.setGuestCount(5);
        assertEquals(5, TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getGuestCount());
    }

    @Test
    public void testSetNote() {
        tableNormal.setNote("Big chocklate cake for Spicces Feri");
        assertEquals("Big chocklate cake for Spicces Feri",
                TableAdapter.getTableByNumber(
                        tableNormal.getAdaptee().getNumber()).getAdaptee().getNote());
    }

    @Test
    public void testDisplayTable() {
        tableNormal.displayTable();
        assertTrue(TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().isVisible());
    }

    @Test
    public void testHideTable() {
        tableNormal.hideTable();
        assertFalse(TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().isVisible());
    }

    @Test
    public void testMoveTable() {
        tableNormal.setPosition(new Point2D(50, 70));
        assertEquals(50, TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCoordinateX());
        assertEquals(70, TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCoordinateY());
    }

    @Test
    public void testRotateTable() {
        tableNormal.rotateTable();
        assertEquals(TableAdapter.getTableByNumber(tableNormal.getAdaptee().getNumber()).getAdaptee().getDimensionX(),
                tableNormal.getAdaptee().getDimensionX());
        assertEquals(TableAdapter.getTableByNumber(tableNormal.getAdaptee().getNumber()).getAdaptee().getDimensionY(),
                tableNormal.getAdaptee().getDimensionY());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testOpenTableAlreadyOpen() {
        tableNormal.openTable();
    }

    @Test
    public void testOpenTable() {
        tableNormalClosed.openTable();
        assertNotNull(tableNormalClosed.getActiveReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testOpenTableForOpen() {
        tableNormal.openTable();
        assertNotNull(tableNormal.getActiveReceipt());
    }

    @Test
    public void testPayTable() {
        tableNormal.payTable(paymentParams);
        assertNull(tableNormal.getActiveReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testPayTableWithClosedTable() {
        tableNormalClosed.payTable(paymentParams);
        assertNull(tableNormalClosed.getActiveReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testPaySelectiveWithClosedTable() {
        tableNormalClosed.paySelective(new ArrayList<>(), paymentParams);
        assertNull(tableNormalClosed.getActiveReceipt());
    }

    @Test
    public void testDeleteTable() {
        tableNormalClosed.deleteTable();
        assertNull(TableAdapter.getTableByNumber(
                tableNormalClosed.getAdaptee().getNumber()));
    }

    @Test(expected = IllegalTableStateException.class)
    public void testDeleteTableWithOpenTable() {
        tableNormal.deleteTable();
        assertNull(TableAdapter.getTableByNumber(
                tableNormal.getAdaptee().getNumber()));
    }

    @Test
    public void testUpdateStock() {
        List<StockParams> paramsList = Arrays.asList(StockParams.builder()
                .productName("productTwo")
                .quantity(Double.valueOf(2))
                .isAbsoluteQuantity(true)
                .build());
        List<Receipt> closedReceiptsBefore = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER,
                query -> {query.setParameter("status", ReceiptStatus.CLOSED);
                    query.setParameter("number", tablePurchase.getAdaptee().getNumber());
                    return query;});
        tablePurchase.updateStock(paramsList, ReceiptType.PURCHASE);
        List<Receipt> closedReceiptsAfter = GuardedTransaction.runNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER,
                query -> {query.setParameter("status", ReceiptStatus.CLOSED);
                    query.setParameter("number", tablePurchase.getAdaptee().getNumber());
                    return query;});

        assertEquals(closedReceiptsBefore.size() + 1, closedReceiptsAfter.size());
    }

    @Test
    public void testGetTablesByType() {
        assertEquals(2, TableAdapter.getTablesByType(TableType.NORMAL).size());
        assertEquals(1, TableAdapter.getTablesByType(TableType.LOITERER).size());
        assertEquals(1, TableAdapter.getTablesByType(TableType.DISPOSAL).size());
    }
}
