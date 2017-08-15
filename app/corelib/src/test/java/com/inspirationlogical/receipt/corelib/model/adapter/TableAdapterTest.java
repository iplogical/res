package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_DISPLAYABLE_TABLES;
import static com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter.getTableFromActual;
import static com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter.getTableFromArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Dimension2D;
import org.junit.Before;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import javafx.geometry.Point2D;

public class TableAdapterTest extends TestBase {

    private TableAdapter tableNormal;
    private TableAdapter tableNormalClosed;
    private TableAdapter tableFrequenter;
    private TableAdapter tableLoiterer;
    private TableAdapter tableAggregate;
    private TableAdapter tableConsumed;
    private TableAdapter tablePurchase;
    private PaymentParams paymentParams;

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
    public void testSetTableParams() {
        TableParams tableParams = TableParams.builder()
                .name("NewTestName")
                .number(tableNormal.getAdaptee().getNumber())
                .note("NewTestNote")
                .guestCount(10)
                .capacity(12)
                .dimension(new Dimension2D(120, 160))
                .build();
        tableNormal.setTableParams(tableParams);
        Table updatedTableNormal = getTableFromActual(tableNormal.getAdaptee().getNumber()).getAdaptee();
        assertEquals("NewTestName", updatedTableNormal.getName());
        assertEquals("NewTestNote", updatedTableNormal.getNote());
        assertEquals(10, updatedTableNormal.getGuestCount());
        assertEquals(12, updatedTableNormal.getCapacity());
        assertEquals(120, updatedTableNormal.getDimensionX());
        assertEquals(160, updatedTableNormal.getDimensionY());
    }

    @Test
    public void testSetTableNumber() {
        int originalNumber = tableNormal.getAdaptee().getNumber();
        tableNormal.setNumber(8);
        assertEquals(8, getTableFromActual(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getNumber());
        assertNull(getTableFromActual(originalNumber));
        assertEquals(8, getTableFromArchive(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getNumber());
        assertNull(getTableFromArchive(originalNumber));
    }

    @Test(expected = RollbackException.class)
    public void testSetTableNumberAlreadyUsed() {
        tableNormal.setNumber(3);
    }

    @Test
    public void testSetGuestCount() {
        tableNormal.setGuestCount(5);
        assertEquals(5, getTableFromActual(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getGuestCount());
    }

    @Test
    public void testDisplayTable() {
        tableNormal.displayTable();
        assertTrue(getTableFromActual(
                tableNormal.getAdaptee().getNumber()).getAdaptee().isVisible());
    }

    @Test
    public void testHideTable() {
        tableNormal.hideTable();
        assertFalse(getTableFromActual(
                tableNormal.getAdaptee().getNumber()).getAdaptee().isVisible());
    }

    @Test
    public void testMoveTable() {
        tableNormal.setPosition(new Point2D(50, 70));
        assertEquals(50, getTableFromActual(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCoordinateX());
        assertEquals(70, getTableFromActual(
                tableNormal.getAdaptee().getNumber()).getAdaptee().getCoordinateY());
    }

    @Test
    public void testRotateTable() {
        tableNormal.rotateTable();
        assertEquals(getTableFromActual(tableNormal.getAdaptee().getNumber()).getAdaptee().getDimensionX(),
                tableNormal.getAdaptee().getDimensionX());
        assertEquals(getTableFromActual(tableNormal.getAdaptee().getNumber()).getAdaptee().getDimensionY(),
                tableNormal.getAdaptee().getDimensionY());
    }

    @Test
    public void testOpenTable() {
        tableNormalClosed.openTable();
        assertNotNull(tableNormalClosed.getOpenReceipt());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testOpenTableAlreadyOpen() {
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
        assertEquals(name, getTableFromActual(tableLoiterer.getAdaptee().getNumber()).getAdaptee().getName());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testPaySelectiveWithClosedTable() {
        tableNormalClosed.paySelective(new ArrayList<>(), paymentParams);
    }

    @Test
    public void testDeleteTable() {
        tableNormalClosed.deleteTable();
        assertNull(getTableFromActual(tableNormalClosed.getAdaptee().getNumber()));
        assertNull(getTableFromArchive(tableNormalClosed.getAdaptee().getNumber()));
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
        assertEquals(12, TableAdapter.getTablesByType(TableType.NORMAL).size());
        assertEquals(1, TableAdapter.getTablesByType(TableType.LOITERER).size());
        assertEquals(1, TableAdapter.getTablesByType(TableType.DISPOSAL).size());
    }

    @Test
    public void testGetFirstUnusedNumber() {
        assertEquals(8, TableAdapter.getFirstUnusedNumber());
        int newFirstUnused = tableNormalClosed.getAdaptee().getNumber();
        tableNormalClosed.setNumber(40);
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
        Table tableLoitererUpdated = getTableFromActual(tableLoiterer.getAdaptee().getNumber()).getAdaptee();
        Table tableNormalClosedUpdated = getTableFromActual(tableNormalClosed.getAdaptee().getNumber()).getAdaptee();
        assertEquals(tableLoitererUpdated.getHost().getNumber(), tableNormalClosedUpdated.getNumber());
        assertEquals(tableLoitererUpdated.getNumber(), tableNormalClosed.getHostedTables().get(0).getNumber());
    }

    @Test
    public void testSetHostHasPrevious() {
        assertEquals(1, tableNormal.getHostedTables().size());
        tableFrequenter.setHost(tableNormalClosed.getAdaptee().getNumber());
        Table tableFrequenterUpdated = getTableFromActual(tableFrequenter.getAdaptee().getNumber()).getAdaptee();
        Table tableNormalClosedUpdated = getTableFromActual(tableNormalClosed.getAdaptee().getNumber()).getAdaptee();
        assertEquals(tableFrequenterUpdated.getHost().getNumber(), tableNormalClosedUpdated.getNumber());
        assertEquals(tableFrequenterUpdated.getNumber(), tableNormalClosed.getHostedTables().get(0).getNumber());
        assertEquals(0, tableNormal.getHostedTables().size());
    }

    @Test
    public void testExchangeTablesOneIsOpen() {
        ReceiptAdapter openReceipt = tableNormal.getOpenReceipt();
        tableNormal.exchangeTables(tableNormalClosed);
        assertNull(tableNormal.getOpenReceipt());
        assertEquals(openReceipt.getAdaptee(), tableNormalClosed.getOpenReceipt().getAdaptee());
    }

    @Test
    public void testExchangeTablesBothAreOpen() {
        GuardedTransaction.run(() -> {
            tableNormalClosed.getAdaptee().getReceipts().forEach(receipt -> {
                receipt.setStatus(ReceiptStatus.OPEN);
                receipt.setClosureTime(null);
            });
        });
        ReceiptAdapter receiptOfTableNormal = tableNormal.getOpenReceipt();
        ReceiptAdapter receiptOfTableNormalClosed = tableNormalClosed.getOpenReceipt();
        tableNormal.exchangeTables(tableNormalClosed);
        assertEquals(receiptOfTableNormal.getAdaptee(), tableNormalClosed.getOpenReceipt().getAdaptee());
        assertEquals(receiptOfTableNormalClosed.getAdaptee(), tableNormal.getOpenReceipt().getAdaptee());

    }
}
