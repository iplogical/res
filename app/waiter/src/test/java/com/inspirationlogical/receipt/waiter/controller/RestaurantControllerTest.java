package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class RestaurantControllerTest extends TestFXBase {


    @Test
    public void testContextMenu() {
        longClickOn("11");
        verifyThatVisible(Resources.WAITER.getString("ContextMenu.OpenTable"));
        verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
        sleep(1500);
    }

    @Test
    public void testContextMenuConfiguration() {
        runInConfigurationMode(() -> {
            longClickOn("11");
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.RotateTable"));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.EditTable"));
        });
        sleep(1000);
    }

    @Test
    public void testContextMenuAddNewTable() {
        runInConfigurationMode(() -> {
            longClickOn(new Point2D(150, 150));
            verifyThatVisible(Resources.WAITER.getString("ContextMenu.AddTable"));
        });
        sleep(1500);
    }

    @Test
    public void testAddNewTableThenDeleteIt() {
        runInConfigurationMode(() -> {
            addTable("TestTableOne", "100");
        });
        sleep(500);
        openThenCloseTable("100");
        runInConfigurationMode(() -> {
            deleteTable("100");
        });
        sleep(1500);
    }

    private void openThenCloseTable(String tableNumber) {
        openTable(tableNumber);
        closeTable(tableNumber);
    }


    private void closeTable(String tableNumber) {
        clickOnThenWait(tableNumber, 500);
        clickOnThenWait(Resources.WAITER.getString("SaleView.ToPaymentView"), 500);
        clickOnThenWait(Resources.WAITER.getString("PaymentView.Pay"), 500);
    }

    private void addTable(String tableName, String number) {
        longClickOn(new Point2D(150, 150));
        clickOnThenWait(Resources.WAITER.getString("ContextMenu.AddTable"), 100);
        ((TextField)find(TABLEFORM_NAME)).setText(tableName);
        ((TextField)find(TABLEFORM_NUMBER)).setText(number);
        ((TextField)find(TABLEFORM_GUEST_COUNT)).setText("26");
        ((TextField)find(TABLEFORM_CAPACITY)).setText("36");
        clickOn(TABLEFORM_CONFIRM);
        verifyThatVisible(tableName);
        verifyThatVisible(number);
        verifyThatVisible("26");
        verifyThatVisible("36");
    }

    private void deleteTable(String tableNumber) {
        longClickOn(tableNumber);
        clickOn(Resources.WAITER.getString("ContextMenu.DeleteTable"));
        verifyThatNotVisible(tableNumber);
    }

    @Test
    public void testAddFrequenterTableThenDeleteIt() {
        runInConfigurationMode(() -> {
            clickOn(Resources.WAITER.getString("Restaurant.Frequenters"));
            addTable("TestTableOne", "100");
            deleteTable("TestTableOne");
            clickOn(Resources.WAITER.getString("Restaurant.Tables"));
        });
        sleep(1500);
    }

    @Test
    public void testAddLoitererTableThenDeleteIt() {
        runInConfigurationMode(() -> {
            clickOn(Resources.WAITER.getString("Restaurant.Loiterers"));
            addTable("TestTableOne", "100");
            deleteTable("TestTableOne");
            clickOn(Resources.WAITER.getString("Restaurant.Tables"));
        });
        sleep(1500);
    }

    @Test
    public void testAddEmployeesTableThenDeleteIt() {
        runInConfigurationMode(() -> {
            clickOn(Resources.WAITER.getString("Restaurant.Employees"));
            addTable("TestTableOne", "100");
            deleteTable("TestTableOne");
            clickOn(Resources.WAITER.getString("Restaurant.Tables"));
        });
        sleep(1500);
    }

    @Test
    public void testOpenThenCloseTable() {
        openThenCloseTable("1");
        sleep(1500);
    }

    @Test
    public void testEditTable() {
        longClickOn("1");
        clickOnThenWait(Resources.WAITER.getString("ContextMenu.EditTable"), 500);
        ((TextField)find(TABLEFORM_NAME)).setText("MyNewName");
        ((TextField)find(TABLEFORM_GUEST_COUNT)).setText("26");
        ((TextField)find(TABLEFORM_CAPACITY)).setText("36");
        ((TextArea)find(TABLEFORM_NOTE)).setText("Hello Table");
        clickOn(TABLEFORM_CONFIRM);
        verifyThatVisible("MyNewName");
        verifyThatVisible("26");
        verifyThatVisible("36");
        openThenCloseTable("1");
        sleep(1500);
    }

    @Test
    public void testMergeTables() {
        runInConfigurationMode(() -> {
            clickOnThenWait("1", 100);
            clickOnThenWait("3", 100);
            longClickOn(new Point2D(150, 150));
            clickOnThenWait(Resources.WAITER.getString("ContextMenu.MergeTable"), 100);
            longClickOn("1");
            clickOnThenWait(Resources.WAITER.getString("ContextMenu.SplitTable"), 100);
        });
        closeTable("1");
        sleep(1500);
    }

    @Test
    public void testEnterDailySummary() {
        clickOnThenWait(Resources.WAITER.getString("Restaurant.Consumption"), 500);
        clickOnThenWait(Resources.WAITER.getString("Common.BackToRestaurantView"), 500);
        sleep(1500);
    }

    @Test
    public void testEnterReservations() {
        clickOnThenWait(Resources.WAITER.getString("Restaurant.Reservation"), 500);
        clickOnThenWait(Resources.WAITER.getString("Common.BackToRestaurantView"), 500);
        sleep(1500);
    }

    @Test
    public void testDailyClosure() {
        clickOnThenWait(Resources.WAITER.getString("Restaurant.DailyClosure"), 500);
        clickOnThenWait("No", 500);
        sleep(1500);
    }
}
