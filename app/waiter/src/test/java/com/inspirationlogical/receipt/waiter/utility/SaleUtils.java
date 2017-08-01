package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.SELECTIVE_CANCELLATION;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.SINGLE_CANCELLATION;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.SOLD_PRODUCTS_TABLE;

public class SaleUtils {

    private static TestFXBase robot;

    public static final String BEER = "Sörök";
    public static final String WINE = "Borok";
    public static final String SOPRONI = "Sop 0,5L";
    public static final String GERE = "Gere";

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

    public static String getProductName(int row) {
        return getSoldProducts().get(row - 1).getProductName();
    }

    public static String getProductQuantity(int row) {
        return getSoldProducts().get(row - 1).getProductQuantity();
    }

    public static String getProductUnitPrice(int row) {
        return getSoldProducts().get(row - 1).getProductUnitPrice();
    }

    public static String getProductTotalPrice(int row) {
        return getSoldProducts().get(row - 1).getProductTotalPrice();
    }

    public static ObservableList<SoldProductViewModel> getSoldProducts() {
        TableView<SoldProductViewModel> tableView = robot.find(SOLD_PRODUCTS_TABLE);
        return tableView.getItems();
    }
}
