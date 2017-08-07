package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.geometry.Point2D;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class RestaurantControllerTest extends TestFXBase {

    @Test
    public void testContextMenu() {
        longClickOn("11");
        verifyThatVisible(Resources.WAITER.getString("ContextMenu.OpenTable"));
        verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
    }

    @Test
    public void testContextMenuConfiguration() {
        runInConfigurationMode(() -> {
            longClickOn("11");
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.RotateTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
        });
    }

    @Test
    public void testContextMenuNoDeleteForOpenTable() {
        openTable("11");
        runInConfigurationMode(() -> {
            longClickOn("11");
            verifyThatNotVisible(Resources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.RotateTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
        });
        closeTable("11");
    }

    @Test
    public void testContextMenuNoDeleteForMergedTable() {
        mergeTables("11", "3");
        runInConfigurationMode(() -> {
            longClickOn("11");
            verifyThatNotVisible(Resources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.RotateTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.SplitTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
            longClickOn("3");
            verifyThatNotVisible(Resources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.RotateTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.SplitTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
        });
        splitTables("11");
        closeTable("11");
    }

    @Test
    public void testContextMenuNoDeleteForHostTable() {
        selectTab("Restaurant.Frequenters");
        addTable("TestTableOne", "11", "1", "1");
        selectTab("Restaurant.Tables");
        runInConfigurationMode(() -> {
            longClickOn("11");
            verifyThatNotVisible(Resources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.RotateTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
        });
        selectTab("Restaurant.Frequenters");
        deleteTable("TestTableOne");
        selectTab("Restaurant.Tables");
    }

    @Test
    public void testContextMenuAddNewTable() {
        runInConfigurationMode(() -> {
            longClickOn(new Point2D(150, 150));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.AddTable"));
        });
    }

    @Test
    public void testEnterDailySummary() {
        clickButtonThenWait("Restaurant.Consumption", 500);
        clickButtonThenWait("Common.BackToRestaurantView", 500);
    }

    @Test
    public void testEnterReservations() {
        clickButtonThenWait("Restaurant.Reservation", 500);
        clickButtonThenWait("Common.BackToRestaurantView", 500);
    }

    @Test
    public void testDailyClosure() {
        clickButtonThenWait("Restaurant.DailyClosure", 500);
        clickOnThenWait("No", 500);
    }
}
