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
    public void testContextMenuAddNewTable() {
        runInConfigurationMode(() -> {
            longClickOn(new Point2D(150, 150));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.AddTable"));
        });
    }

    @Test
    public void testAddNewTableThenDeleteIt() {
        addTable("TestTableOne", "100", "26", "36");
        openThenCloseTable("100");
        deleteTable("100");
    }

    private void openThenCloseTable(String tableNumber) {
        openTable(tableNumber);
        closeTable(tableNumber);
    }

    @Test
    public void testAddFrequenterTableThenDeleteIt() {
        clickOn(Resources.WAITER.getString("Restaurant.Frequenters"));
        addTable("TestTableOne", "100", "26", "36");
        deleteTable("TestTableOne");
        clickOn(Resources.WAITER.getString("Restaurant.Tables"));
    }

    @Test
    public void testAddLoitererTableThenDeleteIt() {
        clickOn(Resources.WAITER.getString("Restaurant.Loiterers"));
        addTable("TestTableOne", "100", "26", "36");
        deleteTable("TestTableOne");
        clickOn(Resources.WAITER.getString("Restaurant.Tables"));
    }

    @Test
    public void testAddEmployeesTableThenDeleteIt() {
        clickOn(Resources.WAITER.getString("Restaurant.Employees"));
        addTable("TestTableOne", "100", "26", "36");
        deleteTable("TestTableOne");
        clickOn(Resources.WAITER.getString("Restaurant.Tables"));
    }

    @Test
    public void testOpenThenCloseTable() {
        openThenCloseTable("1");
    }

    @Test
    public void testEditTable() {
        longClickOn("1");
        clickMenuThenWait("ContextMenu.EditTable", 500);
        setTextField(TABLEFORM_NAME, "MyNewName");
        setTextField(TABLEFORM_GUEST_COUNT, "26");
        setTextField(TABLEFORM_CAPACITY, "36");
        setTextArea(TABLEFORM_NOTE, "Hello Table");
        clickOn(TABLEFORM_CONFIRM);
        verifyThatVisible("MyNewName");
        verifyThatVisible("26");
        verifyThatVisible("36");
        openThenCloseTable("1");
    }

    @Test
    public void testMergeTables() {
        runInConfigurationMode(() -> {
            clickOnThenWait("1", 100);
            clickOnThenWait("3", 100);
            longClickOn(new Point2D(150, 150));
            clickMenuThenWait("ContextMenu.MergeTable", 100);
            longClickOn("1");
            clickMenuThenWait("ContextMenu.SplitTable", 100);
        });
        closeTable("1");
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
