package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.RESTAURANT_TEST_TABLE;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static org.junit.Assert.assertEquals;

public class RestaurantSummaryControllerTest extends TestFXBase {

    private static final String TABLE_NUMBER = RESTAURANT_TEST_TABLE;
    private static final int OPEN_CONSUMPTION = 13100;

    @Before
    public void closeDay() {
        clickButtonThenWait("Restaurant.DailyClosure", 500);
        clickOnThenWait("Yes", 2000);
        sleep(2500);
    }

    @Test
    public void testOpenTableNumber() {
        int originalOpenTableNumber = getOpenTableNumber();
        openTable(TABLE_NUMBER);
        sleep(1000);
        assertEquals(originalOpenTableNumber + 1, getOpenTableNumber());
        closeTable(TABLE_NUMBER);
        assertEquals(originalOpenTableNumber, getOpenTableNumber());
    }

    @Test
    public void testGuestCount() {
        int originalGuestCount = getGuestCount();
        openTable(TABLE_NUMBER);
        enterSaleView(TABLE_NUMBER);
        guestPlus();
        backToRestaurantView();
        assertEquals(originalGuestCount + 1, getGuestCount());
        enterSaleView(TABLE_NUMBER);
        guestMinus();
        backToRestaurantView();
        assertEquals(originalGuestCount, getGuestCount());
        closeTable(TABLE_NUMBER);
    }

    @Test
    public void testIncomeValues() {
        openTableAndSellThreeSoproni();
        backToRestaurantView();
        assertEquals(OPEN_CONSUMPTION + 1320, getOpenConsumption());
        assertEquals(0, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION + 1320, getTotalIncome());
        enterSaleView(TABLE_NUMBER);
        singleCancellation(PRODUCT_FIVE_LONG);
        backToRestaurantView();
        assertEquals(OPEN_CONSUMPTION + 880, getOpenConsumption());
        assertEquals(0, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION + 880, getTotalIncome());
        closeTable(TABLE_NUMBER);
        assertEquals(OPEN_CONSUMPTION , getOpenConsumption());
        assertEquals(880, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION + 880, getTotalIncome());
        openTableAndSellThreeSoproni();
        backToRestaurantView();
        assertEquals(OPEN_CONSUMPTION + 1320, getOpenConsumption());
        assertEquals(880, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION + 2200, getTotalIncome());
        closeTable(TABLE_NUMBER);
        assertEquals(OPEN_CONSUMPTION, getOpenConsumption());
        assertEquals(2200, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION + 2200, getTotalIncome());
    }

    private void openTableAndSellThreeSoproni() {
        openTable(TABLE_NUMBER);
        enterSaleView(TABLE_NUMBER);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 3);
    }
}