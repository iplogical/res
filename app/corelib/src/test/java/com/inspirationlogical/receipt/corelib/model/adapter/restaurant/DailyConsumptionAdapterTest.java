package com.inspirationlogical.receipt.corelib.model.adapter.restaurant;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class DailyConsumptionAdapterTest extends TestBase {

    private RestaurantAdapterImpl restaurantAdapter;
    private Receipt receiptSaleTwo;
    private Receipt receiptSaleFour;
    private Receipt receiptSaleClosedTable;
    private int totalRecordsOfTheDay;
    private LocalDateTime latestClosure;

    @Before
    public void setUp() {
        restaurantAdapter = new RestaurantAdapterImpl(schema.getRestaurant());
        receiptSaleTwo = schema.getReceiptSaleTwo();
        receiptSaleFour = schema.getReceiptSaleFour();
        receiptSaleClosedTable = schema.getReceiptSaleClosedTable();
        int generatedRecords = 6;
        totalRecordsOfTheDay = receiptSaleTwo.getRecords().size() +
                receiptSaleFour.getRecords().size() +
                receiptSaleClosedTable.getRecords().size() +
                generatedRecords;

        latestClosure = DailyClosureAdapter.getLatestClosureTime();
    }

    @Test
    public void testGetConsumptionOfTheDay() {
        assertEquals(7600, restaurantAdapter.getConsumptionOfTheDay(null));
        assertEquals(5600, restaurantAdapter.getConsumptionOfTheDay(PaymentMethod.CASH));
        assertEquals(2000, restaurantAdapter.getConsumptionOfTheDay(PaymentMethod.CREDIT_CARD));
        assertEquals(0, restaurantAdapter.getConsumptionOfTheDay(PaymentMethod.COUPON));
    }

    @Test
    public void testCreateReceiptOfDailyConsumptionNumberOfRecords() {
        Receipt receipt = new DailyConsumptionAdapter().createReceiptOfAggregatedConsumption(latestClosure, now().plusDays(1));
        assertEquals(totalRecordsOfTheDay, receipt.getRecords().size());
    }

    @Test
    public void testCreateReceiptOfDailyConsumptionSameDiscountAndName() {
        receiptSaleTwo.getRecords().get(0).setName("testRecord");
        receiptSaleTwo.getRecords().get(0).setDiscountPercent(20);
        receiptSaleFour.getRecords().get(0).setName("testRecord");
        receiptSaleFour.getRecords().get(0).setDiscountPercent(0);
        Receipt receipt = new DailyConsumptionAdapter().createReceiptOfAggregatedConsumption(latestClosure, now().plusDays(1));
        assertEquals(totalRecordsOfTheDay - 1, receipt.getRecords().size());
    }

    @Test
    public void testCreateReceiptOfDailyConsumptionIncomes() {
        int totalCash = receiptSaleFour.getSumSaleGrossPrice() + receiptSaleClosedTable.getSumSaleGrossPrice();
        int totalCreditCard = receiptSaleTwo.getSumSaleGrossPrice();
        int totalCoupon = 0;
        Receipt receipt = new DailyConsumptionAdapter().createReceiptOfAggregatedConsumption(latestClosure, now().plusDays(1));
        assertEquals(totalCash, getSalePrice(receipt, PaymentMethod.CASH));
        assertEquals(totalCreditCard, getSalePrice(receipt, PaymentMethod.CREDIT_CARD));
        assertEquals(totalCoupon, getSalePrice(receipt, PaymentMethod.COUPON));
    }

    private int getSalePrice(Receipt receipt, PaymentMethod paymentMethod) {
        return receipt.getRecords().stream()
                .filter(record -> record.getName().equals(paymentMethod.toI18nString()))
                .collect(toList()).get(0).getSalePrice();
    }
}
