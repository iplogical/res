package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.geometry.Point2D;
import org.junit.Ignore;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.RESTAURANT_TEST_TABLE;
import static com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantSummaryControllerTest.INITIAL_PAID_CONSUMPTION;
import static com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantSummaryControllerTest.OPEN_CONSUMPTION;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.AGGREGATE_ONE;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.PRODUCT_FIVE;
import static com.inspirationlogical.receipt.waiter.utility.PayUtils.pay;
import static com.inspirationlogical.receipt.waiter.utility.ReservationUtils.backToRestaurantView;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static org.junit.Assert.assertEquals;

public class RestaurantControllerTest extends TestFXBase {

    @Ignore
    @Test
    public void launchAppWithTestDataBase() {
        while (true) {
        }
    }

    @Test
    public void testContextMenu() {
        longClickOn(RESTAURANT_TEST_TABLE);
        verifyMenuItemVisible("ContextMenu.OpenTable");
        verifyMenuItemVisible("ContextMenu.EditTable");
    }

    @Test
    public void testContextMenuConfiguration() {
        runInConfigurationMode(() -> {
            longClickOn(RESTAURANT_TEST_TABLE);
            verifyMenuItemVisible("ContextMenu.DeleteTable");
            verifyMenuItemVisible("ContextMenu.RotateTable");
            verifyMenuItemVisible("ContextMenu.EditTable");
            verifyMenuItemVisible("ContextMenu.ReOpenTable");
        });
    }

    @Test
    public void testContextMenuNoDeleteForOpenTable() {
        openTable(RESTAURANT_TEST_TABLE);
        runInConfigurationMode(() -> {
            longClickOn(RESTAURANT_TEST_TABLE);
            verifyMenuItemNotVisible("ContextMenu.DeleteTable");
            verifyMenuItemVisible("ContextMenu.RotateTable");
            verifyMenuItemVisible("ContextMenu.EditTable");
            verifyMenuItemNotVisible("ContextMenu.ReOpenTable");
        });
        closeTable(RESTAURANT_TEST_TABLE);
    }

    @Test
    public void testContextMenuAddNewTable() {
        runInConfigurationMode(() -> {
            longClickOn(new Point2D(150, 150));
            verifyMenuItemVisible("ContextMenu.AddTable");
        });
    }

    @Test
    public void testEnterDailyConsumption() {
        clickButtonThenWait("Restaurant.Consumption", 500);
        backToRestaurantView();
    }

    @Test
    public void testEnterReservations() {
        enterReservations();
        backToRestaurantView();
    }

    @Ignore
    @Test
    public void testDailyClosureWithYes() {
        assertEquals(INITIAL_PAID_CONSUMPTION, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION, getOpenConsumption());
        openTable(RESTAURANT_TEST_TABLE);
        enterSaleView(RESTAURANT_TEST_TABLE);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 10);
        enterPaymentView();
        pay();
        assertEquals(OPEN_CONSUMPTION, getOpenConsumption());
        assertEquals(4400 + INITIAL_PAID_CONSUMPTION, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION + INITIAL_PAID_CONSUMPTION + 4400, getTotalIncome());
        clickButtonThenWait("Restaurant.DailyClosure", 500);
        clickOnThenWait("Yes", 1000);
        assertEquals(OPEN_CONSUMPTION, getOpenConsumption());
        assertEquals(0, getPaidConsumption());
        assertEquals(OPEN_CONSUMPTION, getTotalIncome());
    }

    private void verifyMenuItemVisible(String menuItem) {
        verifyThatVisible(WaiterResources.WAITER.getString(menuItem));
    }


    private void verifyMenuItemNotVisible(String menuItem) {
        verifyThatNotVisible(WaiterResources.WAITER.getString(menuItem));
    }
}
