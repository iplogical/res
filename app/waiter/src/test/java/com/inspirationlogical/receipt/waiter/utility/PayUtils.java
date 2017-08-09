package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.SELECTIVE_PAYMENT;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.intToForint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PayUtils  extends AbstractUtils {

    public static void pay() {
        clickButtonThenWait(PAY, 2000);
    }

    public static void paySelective(int row) {
        clickButtonThenWait(SELECTIVE_PAYMENT, 50);
        clickOnThenWait(SOLD_POINTS.get(row - 1), 50);
        clickButtonThenWait(SELECTIVE_PAYMENT, 50);
    }

    public static void paySingle(int row) {
        clickButtonThenWait(SINGLE_PAYMENT, 50);
        clickOnThenWait(SOLD_POINTS.get(row - 1), 50);
        clickButtonThenWait(SINGLE_PAYMENT, 50);
    }

    public static void payPartial(int row, double value) {
        setPartialPaymentValue(value);
        payPartial(row);
    }

    public static void setPartialPaymentValue(double value) {
        setTextField(PARTIAL_PAYMENT_VALUE, String.valueOf(value));
    }

    public static void payPartial(int row) {
        clickButtonThenWait(PARTIAL_PAYMENT, 50);
        clickOnThenWait(SOLD_POINTS.get(row - 1), 50);
        clickButtonThenWait(PARTIAL_PAYMENT, 50);
    }

    public static void putBackToSold(int row) {
        clickOnThenWait(PAID_POINTS.get(row - 1), 500);
    }

    public static String getPaidTotalPrice() {
        return getLabel(PAID_TOTAL_PRICE);
    }

    public static String getPreviousPartialPrice() {
        return getLabel(PREVIOUS_PARTIAL_PRICE);
    }

    public static String getPaidProductName(int row) {
        return getPaidProducts().get(row - 1).getProductName();
    }

    public static String getPaidProductQuantity(int row) {
        return getPaidProducts().get(row - 1).getProductQuantity();
    }

    public static String getPaidProductUnitPrice(int row) {
        return getPaidProducts().get(row - 1).getProductUnitPrice();
    }

    public static String getPaidProductTotalPrice(int row) {
        return getPaidProducts().get(row - 1).getProductTotalPrice();
    }

    public static void assertNoPaidProduct() {
        assertTrue(getPaidProducts().isEmpty());
    }

    private static ObservableList<SoldProductViewModel>  getPaidProducts() {
        TableView<SoldProductViewModel> tableView = robot.find(PAID_PRODUCTS_TABLE);
        return tableView.getItems();
    }

    public static void assertPaidProduct(int row, String name, double quantity, int unitPrice, int totalPrice) {
        assertEquals(name, getPaidProductName(row));
        assertEquals(Double.toString(quantity), getPaidProductQuantity(row));
        assertEquals(Integer.toString(unitPrice), getPaidProductUnitPrice(row));
        assertEquals(Integer.toString(totalPrice), getPaidProductTotalPrice(row));
    }

    public static void assertPaidProductFive(int row, double quantity) {
        assertPaidProduct(row, PRODUCT_FIVE_LONG, quantity, 440, (int)(quantity * 440));
    }

    public static void assertPaidProductThree(int row, double quantity) {
        assertPaidProduct(row, PRODUCT_THREE_LONG, quantity, 2900, (int)(quantity * 2900));
    }

    public static void assertPaidTotalPrice(int price) {
        assertEquals(intToForint(price), getPaidTotalPrice());
    }

    public static void assertPreviousPartialPrice(int price) {
        assertEquals(intToForint(price), getPreviousPartialPrice());
    }

    public static void sellGameFee() {
        clickOnThenWait(Resources.WAITER.getString("PaymentView.ManualGameFee"), 50);
    }

    public static void autoGameFee() {
        clickOnThenWait(Resources.WAITER.getString("PaymentView.AutomaticGameFee"), 50);
    }

    public static void setDiscountAbsolute(String text) {
        setTextField(DISCOUNT_ABSOLUTE_VALUE, text);
    }

    public static void setDiscountPercent(String text) {
        setTextField(DISCOUNT_PERCENT_VALUE, text);
    }
}
