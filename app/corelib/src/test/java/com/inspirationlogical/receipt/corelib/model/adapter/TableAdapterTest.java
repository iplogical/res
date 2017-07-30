package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_DISPLAYABLE_TABLES;
import static com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter.getTableByNumber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import javafx.geometry.Dimension2D;
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

    private TableAdapter tableNormal;
    private TableAdapter tableNormalClosed;
    private TableAdapter tableFrequenter;
    private TableAdapter tableLoiterer;
    private TableAdapter tableAggregate;
    private TableAdapter tableConsumed;
    private TableAdapter tablePurchase;
    private PaymentParams paymentParams;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        tableNormal = new TableAdapter(schema.getTableNormal());
        tableNormalClosed = new TableAdapter(schema.getTableNormalClosed());
        tableFrequenter = new TableAdapter(schema.getTableFrequenter());
        tableLoiterer = new TableAdapter(schema.getTableLoiterer());
        tableAggregate = new TableAdapter(schema.getTableConsumer());
        tableConsumed = new TableAdapter(schema.getTableConsumed());
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
        assertNotNull(tableNormal.getOpenReceipt());
    }

    @Test
    public void testGetDisplayableTables() {
        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES, TableAdapter.getDisplayableTables().size());
    }

    @Test
    public void testSetTableName() {
        tableNormal.setName("New Table Name");
        assertEquals("New Table Name", getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getName());
    }

    @Test
    public void testSetTableNumber() {
        tableNormal.setNumber(8);
        assertEquals(8, getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getNumber());
    }

    @Test(expected = RollbackException.class)
    public void testSetTableNumberAlreadyUsed() {
        tableNormal.setNumber(3);
    }

    @Test
    public void testSetTableType() {
        tableNormal.setType(TableType.LOITERER);
        assertEquals(TableType.LOITERER, getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getType());
    }

    @Test
    public void testSetCapacity() {
        tableNormal.setCapacity(10);
        assertEquals(10, getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCapacity());
    }

    @Test
    public void testSetGuestCount() {
        tableNormal.setGuestCount(5);
        assertEquals(5, getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getGuestCount());
    }

    @Test
    public void testSetNote() {
        tableNormal.setNote("Big chocklate cake for Spicces Feri");
        assertEquals("Big chocklate cake for Spicces Feri",
                getTableByNumber(
                        tableNormal.getAdaptee().getNumber()).getAdaptee().getNote());
    }

    @Test
    public void testDisplayTable() {
        tableNormal.displayTable();
        assertTrue(getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().isVisible());
    }

    @Test
    public void testHideTable() {
        tableNormal.hideTable();
        assertFalse(getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().isVisible());
    }

    @Test
    public void testMoveTable() {
        tableNormal.setPosition(new Point2D(50, 70));
        assertEquals(50, getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCoordinateX());
        assertEquals(70, getTableByNumber(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCoordinateY());
    }

    @Test
    public void testSetDimension() {
        tableNormal.setDimension(new Dimension2D(110, 120));
        Table tableNormalUpdated = getTableByNumber(tableNormal.getAdaptee().getNumber()).getAdaptee();
        assertEquals(110, tableNormalUpdated.getDimensionX());
        assertEquals(120, tableNormalUpdated.getDimensionY());
    }
    @Test
    public void testRotateTable() {
        tableNormal.rotateTable();
        assertEquals(getTableByNumber(tableNormal.getAdaptee().getNumber()).getAdaptee().getDimensionX(),
                tableNormal.getAdaptee().getDimensionX());
        assertEquals(getTableByNumber(tableNormal.getAdaptee().getNumber()).getAdaptee().getDimensionY(),
                tableNormal.getAdaptee().getDimensionY());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testOpenTableAlreadyOpen() {
        tableNormal.openTable();
    }

    @Test
    public void testOpenTable() {
        tableNormalClosed.openTable();
        assertNotNull(tableNormalClosed.getOpenReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testOpenTableForOpen() {
        tableNormal.openTable();
    }

    @Test
    public void testPayTable() {
        tableNormal.payTable(paymentParams);
        assertNull(tableNormal.getOpenReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testPayTableWithClosedTable() {
        tableNormalClosed.payTable(paymentParams);
    }

    @Test
    public void testPayTableForVirtualTable() {
        String name = tableLoiterer.getAdaptee().getName();
        tableLoiterer.payTable(paymentParams);
        assertEquals(name, getTableByNumber(tableLoiterer.getAdaptee().getNumber()).getAdaptee().getName());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testPaySelectiveWithClosedTable() {
        tableNormalClosed.paySelective(new ArrayList<>(), paymentParams);
    }

    @Test
    public void testDeleteTable() {
        tableNormalClosed.deleteTable();
        assertNull(getTableByNumber(
                tableNormalClosed.getAdaptee().getNumber()));
    }

    @Test(expected = IllegalTableStateException.class)
    public void testDeleteTableWithOpenTable() {
        tableNormal.deleteTable();
    }

    @Test(expected = IllegalTableStateException.class)
    public void testDeleteTableWithConsumer() {
        tableAggregate.deleteTable();
    }

    @Test
    public void testIsTableConsumer() {
        assertTrue(tableAggregate.isConsumerTable());
        assertFalse(tableNormalClosed.isConsumerTable());
    }

    @Test
    public void testIsTableConsumed() {
        assertTrue(tableConsumed.isTableConsumed());
        assertFalse(tableNormal.isTableConsumed());
    }

    @Test
    public void testIsTableHost() {
        assertTrue(tableNormal.isTableHost());
        assertFalse(tableNormalClosed.isTableHost());
    }

    @Test
    public void testIsTableHosted() {
        assertTrue(tableFrequenter.isTableHosted());
        assertFalse(tableNormalClosed.isTableHosted());
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
        assertEquals(4, TableAdapter.getTablesByType(TableType.NORMAL).size());
        assertEquals(1, TableAdapter.getTablesByType(TableType.LOITERER).size());
        assertEquals(1, TableAdapter.getTablesByType(TableType.DISPOSAL).size());
    }

    @Test
    public void testGetFirstUnusedNumber() {
        assertEquals(8, TableAdapter.getFirstUnusedNumber());
        int newFirstUnused = tableNormalClosed.getAdaptee().getNumber();
        tableNormalClosed.setNumber(20);
        assertEquals(newFirstUnused, TableAdapter.getFirstUnusedNumber());
    }

    @Test
    public void testGetHost() {
        assertEquals(tableNormal.getAdaptee().getNumber(), tableFrequenter.getHost().getAdaptee().getNumber());
        assertNull(tableNormalClosed.getHost());
    }

    @Test
    public void testSetHostHasNoPrevious() {
        tableLoiterer.setHost(tableNormalClosed.getAdaptee().getNumber());
        Table tableLoitererUpdated = getTableByNumber(tableLoiterer.getAdaptee().getNumber()).getAdaptee();
        Table tableNormalClosedUpdated = getTableByNumber(tableNormalClosed.getAdaptee().getNumber()).getAdaptee();
        assertEquals(tableLoitererUpdated.getHost().getNumber(), tableNormalClosedUpdated.getNumber());
        assertEquals(tableLoitererUpdated.getNumber(), tableNormalClosed.getHostedTables().get(0).getNumber());
    }

    @Test
    public void testSetHostHasPrevious() {
        assertEquals(1, tableNormal.getHostedTables().size());
        tableFrequenter.setHost(tableNormalClosed.getAdaptee().getNumber());
        Table tableFrequenterUpdated = getTableByNumber(tableFrequenter.getAdaptee().getNumber()).getAdaptee();
        Table tableNormalClosedUpdated = getTableByNumber(tableNormalClosed.getAdaptee().getNumber()).getAdaptee();
        assertEquals(tableFrequenterUpdated.getHost().getNumber(), tableNormalClosedUpdated.getNumber());
        assertEquals(tableFrequenterUpdated.getNumber(), tableNormalClosed.getHostedTables().get(0).getNumber());
        assertEquals(0, tableNormal.getHostedTables().size());
    }
}
