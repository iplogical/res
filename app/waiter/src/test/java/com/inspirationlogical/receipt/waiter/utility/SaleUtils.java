package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.getLabel;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.GERE_LONG;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.SOPRONI_LONG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaleUtils {

    private static TestFXBase robot;

    public static void setRobot(TestFXBase robot) {
        SaleUtils.robot = robot;
    }

    public static void selectCategory(String categoryName) {
        clickOnThenWait(categoryName, 50);
    }

    public static void sellProduct(String productName) {
        clickOnThenWait(productName, 50);
    }

    public static void sellProduct(String productName, int quantity) {
        for(int i = 0; i < quantity; i++)
            sellProduct(productName);
    }

    public static void selectiveCancellation(String productName) {
        clickButtonThenWait(SELECTIVE_CANCELLATION, 30);
        clickOnThenWait(productName, 30);
        clickButtonThenWait(SELECTIVE_CANCELLATION, 30);
    }

    public static void singleCancellation(String productName) {
        clickButtonThenWait(SINGLE_CANCELLATION, 30);
        clickOnThenWait(productName, 30);
        clickButtonThenWait(SINGLE_CANCELLATION, 30);
    }

    public static String getSoldProductName(int row) {
        return getSoldProducts().get(row - 1).getProductName();
    }

    public static String getSoldProductQuantity(int row) {
        return getSoldProducts().get(row - 1).getProductQuantity();
    }

    public static String getSoldProductUnitPrice(int row) {
        return getSoldProducts().get(row - 1).getProductUnitPrice();
    }

    public static String getSoldProductTotalPrice(int row) {
        return getSoldProducts().get(row - 1).getProductTotalPrice();
    }
    
    public static void assertNoSoldProduct() {
        assertTrue(getSoldProducts().isEmpty());
    }
    
    public static ObservableList<SoldProductViewModel> getSoldProducts() {
        TableView<SoldProductViewModel> tableView = robot.find(SOLD_PRODUCTS_TABLE);
        return tableView.getItems();
    }

    public static void assertSoldProduct(int row, String name, double quantity, int unitPrice, int totalPrice) {
        assertEquals(name, getSoldProductName(row));
        assertEquals(Double.toString(quantity), getSoldProductQuantity(row));
        assertEquals(Integer.toString(unitPrice), getSoldProductUnitPrice(row));
        assertEquals(Integer.toString(totalPrice), getSoldProductTotalPrice(row));
    }

    public static void assertSoldSoproni(int row, double quantity) {
        assertSoldProduct(row, SOPRONI_LONG, quantity, 440, (int)(quantity * 440));
    }

    public static void assertSoldGere(int row, double quantity) {
        assertSoldProduct(row, GERE_LONG, quantity, 2900, (int)(quantity * 2900));
    }


    public static void assertNumberOfSoldProducts(int expected) {
        assertEquals(expected, getSoldProducts().size());
    }

    public static void assertSoldTotalPrice(int price) {
        assertEquals(intToForint(price), getSoldTotalPrice());
    }

    private static String getSoldTotalPrice() {
        return getLabel(SOLD_TOTAL_PRICE);
    }

    public static String intToForint(int price) {
        return Integer.toString(price) + " Ft";
    }
}
