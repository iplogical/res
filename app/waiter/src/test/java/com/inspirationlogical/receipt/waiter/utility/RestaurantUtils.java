package com.inspirationlogical.receipt.waiter.utility;

import javafx.geometry.Point2D;
import javafx.scene.control.TextField;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;

public class RestaurantUtils  extends AbstractUtils {

    public static void enterSaleView(String table) {
        clickOnThenWait(table, 200);
    }

    public static void enterReservations() {
        clickButtonThenWait("Restaurant.Reservation", 500);
    }

    public static void enterDailySummary() {
        clickButtonThenWait("Restaurant.Consumption", 500);
    }

    public static void openTable(String tableNumber) {
        longClickOn(tableNumber);
        clickOnThenWait(WaiterResources.WAITER.getString("ContextMenu.OpenTable"), 500);
    }

    public static void reOpenTable(String tableNumber) {
        runInConfigurationMode(() -> {
            longClickOn(tableNumber);
            clickOnThenWait(WaiterResources.WAITER.getString("ContextMenu.ReOpenTable"), 500);
        });
    }

    public static void closeTable(String tableNumber) {
        clickOnThenWait(tableNumber, 500);
        clickButtonThenWait("SaleView.ToPaymentView", 500);
        clickButtonThenWait("PaymentView.Pay", 500);
    }

    public static void addTable(String tableName, String number, String guestCount, String capacity) {
        runInConfigurationMode(() -> {
            longClickOn(new Point2D(600, 150));
            clickOnThenWait(WaiterResources.WAITER.getString("ContextMenu.AddTable"), 500);
            ((TextField)robot.find(TABLEFORM_NAME)).setText(tableName);
            ((TextField)robot.find(TABLEFORM_NUMBER)).setText(number);
            ((TextField)robot.find(TABLEFORM_GUEST_COUNT)).setText(guestCount);
            ((TextField)robot.find(TABLEFORM_CAPACITY)).setText(capacity);
            clickOnThenWait(TABLEFORM_CONFIRM, 100);
            verifyThatVisible(tableName);
            verifyThatVisible(number);
            verifyThatVisible(guestCount);
            verifyThatVisible(capacity);
        });
    }

    public static void deleteTable(String tableNumber) {
        runInConfigurationMode(() -> {
            longClickOn(tableNumber);
            robot.clickOn(WaiterResources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatNotVisible(tableNumber);
        });
    }

    public static void selectTab(String tab) {
        robot.clickOn(WaiterResources.WAITER.getString(tab));
    }

    public static void addTableToTab(String tableName, String number, String tab) {
        selectTab(tab);
        addTable(tableName, number, "1", "1");
        selectTab("Restaurant.Tables");
    }

    public static void deleteTableFromTab(String tableName, String tab) {
        selectTab(tab);
        deleteTable(tableName);
        selectTab("Restaurant.Tables");
    }

    public static int getOpenTableNumber() {
        return Integer.valueOf(getLabel(OPEN_TABLE_NUMBER));
    }

    public static int getGuestCount() {
        return Integer.valueOf(getLabel(TOTAL_GUESTS));
    }

    public static int getOpenConsumption() {
        return Integer.valueOf(getLabel(OPEN_CONSUMPTION));
    }

    public static int getPaidConsumption() {
        return Integer.valueOf(getLabel(PAID_CONSUMPTION));
    }

    public static int getTotalIncome() {
        return Integer.valueOf(getLabel(TOTAL_INCOME));
    }

    public static void closeDay() {
//        clickButtonThenWait("Restaurant.DailyClosure", 500);
        clickButtonThenWait(CLOSE_DAY, 500);
        clickOnThenWait("Yes", 2000);
        robot.sleep(2500);
    }
}
