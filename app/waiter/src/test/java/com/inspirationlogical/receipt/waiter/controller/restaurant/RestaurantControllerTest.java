package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.geometry.Point2D;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.CONSUMED_TEST_TABLE_ONE;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.RESTAURANT_TEST_TABLE;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.ReservationUtils.backToRestaurantView;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class RestaurantControllerTest extends TestFXBase {

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
        });
        closeTable(RESTAURANT_TEST_TABLE);
    }

    @Test
    public void testContextMenuNoDeleteForMergedTable() {
        mergeTables(RESTAURANT_TEST_TABLE, CONSUMED_TEST_TABLE_ONE);
        runInConfigurationMode(() -> {
            longClickOn(RESTAURANT_TEST_TABLE);
            verifyMenuItemNotVisible("ContextMenu.DeleteTable");
            verifyMenuItemVisible("ContextMenu.RotateTable");
            verifyMenuItemVisible("ContextMenu.SplitTable");
            verifyMenuItemVisible("ContextMenu.EditTable");
            longClickOn(CONSUMED_TEST_TABLE_ONE);
            verifyMenuItemNotVisible("ContextMenu.DeleteTable");
            verifyMenuItemVisible("ContextMenu.RotateTable");
            verifyMenuItemVisible("ContextMenu.SplitTable");
            verifyMenuItemVisible("ContextMenu.EditTable");
        });
        splitTables(RESTAURANT_TEST_TABLE);
        closeTable(RESTAURANT_TEST_TABLE);
    }

    @Test
    public void testContextMenuNoDeleteForHostTable() {
        addTableToTab("TestTableOne", RESTAURANT_TEST_TABLE, "Restaurant.Frequenters");
        runInConfigurationMode(() -> {
            longClickOn(RESTAURANT_TEST_TABLE);
            verifyMenuItemNotVisible("ContextMenu.DeleteTable");
            verifyMenuItemVisible("ContextMenu.RotateTable");
            verifyMenuItemVisible("ContextMenu.EditTable");
        });
        deleteTableFromTab("TestTableOne", "Restaurant.Frequenters");
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

    @Test
    public void testDailyClosure() {
        clickButtonThenWait("Restaurant.DailyClosure", 500);
        clickOnThenWait("No", 500);
    }

    private void verifyMenuItemVisible(String menuItem) {
        verifyThatVisible(Resources.WAITER.getString(menuItem));
    }


    private void verifyMenuItemNotVisible(String menuItem) {
        verifyThatNotVisible(Resources.WAITER.getString(menuItem));
    }
}
