package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.setTextField;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaleUtils  extends AbstractUtils {

    public static void selectCategory(String categoryName) {
        clickOnThenWait(categoryName, 50);
    }

    public static void sellProduct(String productName) {
        clickOnThenWait(productName, 30);
    }

    public static void sellProduct(String productName, int quantity) {
        for(int i = 0; i < quantity; i++)
            sellProduct(productName);
    }

    public static void sellAdHocProduct(String name, int quantity, int purchasePrice, int salePrice) {
        clickButtonThenWait(SELL_ADHOC_PRODUCT, 100);
        fillAdHocFormAndConfirm(name, quantity, purchasePrice, salePrice);
    }

    private static void fillAdHocFormAndConfirm(String name, int quantity, int purchasePrice, int salePrice) {
        setTextField(ADHOC_PRODUCT_NAME, name);
        setTextField(ADHOC_PRODUCT_QUANTITY, Integer.toString(quantity));
        setTextField(ADHOC_PRODUCT_PURCHASE_PRICE, Integer.toString(purchasePrice));
        setTextField(ADHOC_PRODUCT_SALE_PRICE, Integer.toString(salePrice));
        clickButtonThenWait("Form.Confirm", 100);
    }

    public static void selectiveCancellation(String productName) {
        clickButtonThenWait(SELECTIVE_CANCELLATION, 50);
        clickOnThenWait(productName, 50);
        clickButtonThenWait(SELECTIVE_CANCELLATION, 50);
    }

    public static void singleCancellation(String productName) {
        clickButtonThenWait(SINGLE_CANCELLATION, 50);
        clickOnThenWait(productName, 50);
        clickButtonThenWait(SINGLE_CANCELLATION, 50);
    }

    public static String getSoldProductName(int row) {
        return getSoldProducts().get(row - 1).getProductName();
    }

    private static String getSoldProductQuantity(int row) {
        return getSoldProducts().get(row - 1).getProductQuantity();
    }

    private static String getSoldProductUnitPrice(int row) {
        return getSoldProducts().get(row - 1).getProductUnitPrice();
    }

    private static String getSoldProductTotalPrice(int row) {
        return getSoldProducts().get(row - 1).getProductTotalPrice();
    }
    
    public static void assertNoSoldProduct() {
        assertTrue(getSoldProducts().isEmpty());
    }
    
    private static ObservableList<SoldProductViewModel> getSoldProducts() {
        TableView<SoldProductViewModel> tableView = robot.find(SOLD_PRODUCTS_TABLE);
        return tableView.getItems();
    }

    public static void assertSoldProduct(int row, String name, double quantity, int unitPrice, int totalPrice) {
        assertEquals(name, getSoldProductName(row));
        assertEquals(Double.toString(quantity), getSoldProductQuantity(row));
        assertEquals(Integer.toString(unitPrice), getSoldProductUnitPrice(row));
        assertEquals(Integer.toString(totalPrice), getSoldProductTotalPrice(row));
    }

    public static void assertSoldProductFive(int row, double quantity) {
        assertSoldProduct(row, PRODUCT_FIVE_LONG, quantity, 440, (int)Math.round(quantity * 440));
    }

    public static void assertSoldProductThree(int row, double quantity) {
        assertSoldProduct(row, PRODUCT_THREE_LONG, quantity, 2900, (int)Math.round(quantity * 2900));
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

    public static void guestMinus() {
        clickOnThenWait(GUEST_MINUS, 100);
    }

    public static void guestPlus() {
        clickOnThenWait(GUEST_PLUS, 100);
    }

    public static void enterPaymentView() {
        clickButtonThenWait(TO_PAYMENT, 200);
    }

    public static void clickOnGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
    }

    public static void backToRestaurantView() {
        clickButtonThenWait("Common.BackToRestaurantView", 500);
    }
}
