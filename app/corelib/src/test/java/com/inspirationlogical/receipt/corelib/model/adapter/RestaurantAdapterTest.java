package com.inspirationlogical.receipt.corelib.model.adapter;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_DISPLAYABLE_TABLES;
import static com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter.getTableByNumber;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import java.util.Arrays;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapterTest {

    private RestaurantAdapter restaurantAdapter;
    private Table.TableBuilder tableBuilder;
    private TableAdapter tableNormal;
    private TableAdapter tableNormalClosed;
    private TableAdapter tableConsumer;
    private TableAdapter tableConsumed;
    private Receipt receiptSaleTwo;
    private Receipt receiptSaleFour;
    private Receipt receiptSaleClosedTable;
    private int totalRecordsOfTheDay;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        restaurantAdapter = new RestaurantAdapter(schema.getRestaurant());
        tableNormal = new TableAdapter(schema.getTableNormal());
        tableNormalClosed = new TableAdapter(schema.getTableNormalClosed());
        tableConsumer = new TableAdapter(schema.getTableConsumer());
        tableConsumed = new TableAdapter(schema.getTableConsumed());
        receiptSaleTwo = schema.getReceiptSaleTwo();
        receiptSaleFour = schema.getReceiptSaleFour();
        receiptSaleClosedTable = schema.getReceiptSaleClosedTable();
        int generatedRecords = 3;
        totalRecordsOfTheDay = receiptSaleTwo.getRecords().size() +
                receiptSaleFour.getRecords().size() +
                receiptSaleClosedTable.getRecords().size() +
                generatedRecords;

        tableBuilder =  Table.builder()
                .name("Ittas Juci")
                .number(88)
                .type(TableType.NORMAL)
                .coordinateX(20)
                .coordinateY(20)
                .guestCount(5)
                .capacity(5)
                .note("Big Chocklate Cake")
                .visible(true);
    }

    @Test
    public void testGetActiveRestaurant() {
        assertNotNull(RestaurantAdapter.getActiveRestaurant());
    }

    @Test
    public void testAddTable() {
        restaurantAdapter.addTable(tableBuilder);
        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES + 1, TableAdapter.getDisplayableTables().size());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testAddTableNumberAlreadyUsed() {
        tableBuilder.number(schema.getTableNormal().getNumber());
        restaurantAdapter.addTable(tableBuilder);
    }

    @Test
    public void testAddTableNumberAlreadyUsedHost() {
        int firstUnsued = TableAdapter.getFirstUnusedNumber();
        tableBuilder.number(schema.getTableNormal().getNumber())
                .type(TableType.LOITERER);
        restaurantAdapter.addTable(tableBuilder);
        assertNotNull(getTableByNumber(firstUnsued));
        assertTrue(getTableByNumber(schema.getTableNormal().getNumber()).isTableHost());
        assertEquals(NUMBER_OF_DISPLAYABLE_TABLES + 1, TableAdapter.getDisplayableTables().size());
    }

    @Test
    public void testMergeTables() {
        restaurantAdapter.mergeTables(tableNormal, Arrays.asList(tableNormalClosed));
        Table tableNormalUpdated = getTableByNumber(tableNormal.getAdaptee().getNumber()).getAdaptee();
        Table tableNormalClosedUpdated = getTableByNumber(tableNormalClosed.getAdaptee().getNumber()).getAdaptee();
        assertEquals(1,
                tableNormalUpdated.getConsumed().stream()
                        .filter(table -> table.getNumber() == tableNormalClosed.getAdaptee().getNumber())
                        .collect(toList()).size());
        assertEquals(tableNormal.getAdaptee().getNumber(), tableNormalClosedUpdated.getConsumer().getNumber());
    }

    @Test
    public void testMergerTablesMoveReceiptRecords() {
        int recordNum = tableNormal.getOpenReceipt().getAdaptee().getRecords().size();
        restaurantAdapter.mergeTables(tableNormalClosed, Arrays.asList(tableNormal));
        TableAdapter tableNormalUpdated = getTableByNumber(tableNormal.getAdaptee().getNumber());
        TableAdapter tableNormalClosedUpdated = getTableByNumber(tableNormalClosed.getAdaptee().getNumber());
        assertNull(tableNormalUpdated.getOpenReceipt());
        assertNotNull(tableNormalClosedUpdated.getOpenReceipt());
        assertEquals(recordNum, tableNormalClosedUpdated.getOpenReceipt().getAdaptee().getRecords().size());
    }

    @Test(expected = IllegalTableStateException.class)
    public void testMergeTablesConsumerTable() {
        restaurantAdapter.mergeTables(tableNormalClosed, Arrays.asList(tableConsumer));
    }

    @Test
    public void testSplitTables() {
        restaurantAdapter.splitTables(tableConsumer);
        TableAdapter tableConsumerUpdated = getTableByNumber(tableConsumer.getAdaptee().getNumber());
        TableAdapter tableConsumedUpdated = getTableByNumber(tableConsumed.getAdaptee().getNumber());
        assertNull(tableConsumedUpdated.getAdaptee().getConsumer());
        assertEquals(0, tableConsumerUpdated.getAdaptee().getConsumed().size());
    }

    @Test
    public void testGetConsumptionOfTheDay() {
        assertEquals(6000, restaurantAdapter.getConsumptionOfTheDay(receipt ->
                receipt.getAdaptee().getPaymentMethod().equals(PaymentMethod.CASH)));
        assertEquals(2000, restaurantAdapter.getConsumptionOfTheDay(receipt ->
                receipt.getAdaptee().getPaymentMethod().equals(PaymentMethod.CREDIT_CARD)));
        assertEquals(0, restaurantAdapter.getConsumptionOfTheDay(receipt ->
                receipt.getAdaptee().getPaymentMethod().equals(PaymentMethod.COUPON)));
    }

    @Test
    public void testCreateReceiptOfDailyConsumptionNumberOfRecords() {
        ReceiptAdapter receipt = restaurantAdapter.createReceiptOfDailyConsumption();
        assertEquals(totalRecordsOfTheDay, receipt.getAdaptee().getRecords().size());
    }

    @Test
    public void testCreateReceiptOfDailyConsumptionSameDiscountAndName() {
        receiptSaleTwo.getRecords().get(0).setName("testRecord");
        receiptSaleTwo.getRecords().get(0).setDiscountPercent(20);
        receiptSaleFour.getRecords().get(0).setName("testRecord");
        receiptSaleFour.getRecords().get(0).setDiscountPercent(20);
        ReceiptAdapter receipt = restaurantAdapter.createReceiptOfDailyConsumption();
        assertEquals(totalRecordsOfTheDay - 1, receipt.getAdaptee().getRecords().size());
    }

    @Test
    public void testCreateReceiptOfDailyConsumptionIncomes() {
        int totalCash = receiptSaleFour.getSumSaleGrossPrice() + receiptSaleClosedTable.getSumSaleGrossPrice();
        int totalCreditCard = receiptSaleTwo.getSumSaleGrossPrice();
        int totalCoupon = 0;
        ReceiptAdapter receipt = restaurantAdapter.createReceiptOfDailyConsumption();
        assertEquals(totalCash, getSalePrice(receipt, PaymentMethod.CASH));
        assertEquals(totalCreditCard, getSalePrice(receipt, PaymentMethod.CREDIT_CARD));
        assertEquals(totalCoupon, getSalePrice(receipt, PaymentMethod.COUPON));
    }

    private int getSalePrice(ReceiptAdapter receipt, PaymentMethod paymentMethod) {
        return receipt.getAdaptee().getRecords().stream()
                        .filter(record -> record.getName().equals(paymentMethod.toString()))
                        .collect(toList()).get(0).getSalePrice();
    }
}
