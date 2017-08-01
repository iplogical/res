package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class SaleControllerTest extends TestFXBase {

    private static final String TABLE_NAME = "TestTableName20";
    private static final String TABLE_NUMBER = "20";
    private static final String TABLE_GUESTS = "0";
    private static final String TABLE_CAPACITY = "4";

    private static final String BEER = "Sörök";
    private static final String SOPRONI = "Sop 0,5L";

    @Before
    public void setUpSaleControllerTest() throws Exception {
//        addTable(TABLE_NAME, TABLE_NUMBER, TABLE_GUESTS, TABLE_CAPACITY);
//        openTable(TABLE_NUMBER);
        clickOnThenWait(TABLE_NUMBER, 200);
    }

    @Test
    public void testClickOnGuestPlus() {
        clickOnThenWait(GUEST_PLUS, 100);
        assertEquals(TABLE_NUMBER + " (" + (Integer.valueOf(TABLE_GUESTS) + 1) + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        clickOnThenWait(GUEST_MINUS, 100);
        assertEquals(TABLE_NUMBER + " (" + TABLE_GUESTS + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
    }

    @Test
    public void testSelectCategory() {
        selectCategory(BEER);
        verifyThatVisible(SOPRONI);
    }

    @Test
    public void testSellProduct() {
        selectCategory(BEER);
        sellProduct(SOPRONI);
        assertEquals("Soproni 0,5L", getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("440", getProductTotalPrice(1));
        selectiveCancellation("Soproni 0,5L");
    }

    private void selectCategory(String categoryName) {
        clickOnThenWait(categoryName, 50);
    }

    private void sellProduct(String productName) {
        clickOnThenWait(productName, 50);
    }

    private void sellProduct(String productName, int quantity) {
        for(int i = 0; i < quantity; i++)
            sellProduct(productName);
    }

    private void selectiveCancellation(String productName) {
        clickButtonThenWait(SELECTIVE_CANCELLATION, 30);
        clickOnThenWait(productName, 30);
        clickButtonThenWait(SELECTIVE_CANCELLATION, 30);
    }

    private void singleCancellation(String productName) {
        clickButtonThenWait(SINGLE_CANCELLATION, 30);
        clickOnThenWait(productName, 30);
        clickButtonThenWait(SINGLE_CANCELLATION, 30);
    }

    @Test
    public void testSellAdHocProduct() {
        clickButtonThenWait(SELL_ADHOC_PRODUCT, 100);
        setTextField(ADHOC_PRODUCT_NAME, "TestAdHocProductName");
        setTextField(ADHOC_PRODUCT_QUANTITY, "2");
        setTextField(ADHOC_PRODUCT_PURCHASE_PRICE, "200");
        setTextField(ADHOC_PRODUCT_SALE_PRICE, "400");
        clickButtonThenWait("Form.Confirm", 100);
        assertEquals("TestAdHocProductName", getProductName(1));
        assertEquals("2.0", getProductQuantity(1));
        assertEquals("400", getProductUnitPrice(1));
        assertEquals("800", getProductTotalPrice(1));
        selectiveCancellation("TestAdHocProductName");
    }

    @Test
    public void testSellGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEER);
        sellProduct(SOPRONI);
        assertEquals("Soproni 0,5L *", getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectiveCancellation("Soproni 0,5L *");
    }

    @Test
    public void testSellMultipleGiftProductsWithin5Seconds() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEER);
        sellProduct(SOPRONI);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        sellProduct(SOPRONI);
        assertEquals("Soproni 0,5L *", getProductName(1));
        assertEquals("2.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
        selectiveCancellation("Soproni 0,5L *");
    }

    @Test
    public void testSellGiftProductThenNormalProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEER);
        sellProduct(SOPRONI);
        clickButtonThenWait(GIFT_PRODUCT, 5100);
        sellProduct(SOPRONI);
        assertEquals("Soproni 0,5L *", getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
        assertEquals("Soproni 0,5L", getProductName(2));
        assertEquals("1.0", getProductQuantity(2));
        assertEquals("440", getProductUnitPrice(2));
        assertEquals("440", getProductTotalPrice(2));
        selectiveCancellation("Soproni 0,5L *");
        selectiveCancellation("Soproni 0,5L");
    }

    @Test
    public void testSelectiveCancellation() {
        selectCategory(BEER);
        sellProduct(SOPRONI, 5);
        assertEquals("Soproni 0,5L", getProductName(1));
        assertEquals("5.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("2200", getProductTotalPrice(1));

        selectiveCancellation("Soproni 0,5L");
        assertSoldProductsEmpty();
    }

    @Test
    public void testSingleCancellation() {
        selectCategory(BEER);
        sellProduct(SOPRONI, 3);
        assertEquals("Soproni 0,5L", getProductName(1));
        assertEquals("3.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("1320", getProductTotalPrice(1));

        singleCancellation("Soproni 0,5L");
        assertEquals("Soproni 0,5L", getProductName(1));
        assertEquals("2.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("880", getProductTotalPrice(1));

        singleCancellation("Soproni 0,5L");
        assertEquals("Soproni 0,5L", getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("440", getProductTotalPrice(1));

        singleCancellation("Soproni 0,5L");
        assertSoldProductsEmpty();
    }

    private void assertSoldProductsEmpty() {
        assertTrue(getSoldProducts().isEmpty());
    }

    @Test
    public void testIncreaseGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEER);
        sellProduct(SOPRONI);
        clickOnThenWait("Soproni 0,5L *", 20);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        clickOnThenWait("Soproni 0,5L *", 20);
        assertEquals("Soproni 0,5L *", getProductName(1));
        assertEquals("3.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
        selectiveCancellation("Soproni 0,5L *");
    }

    private String getProductName(int row) {
        return getSoldProducts().get(row - 1).getProductName();
    }

    private String getProductQuantity(int row) {
        return getSoldProducts().get(row - 1).getProductQuantity();
    }

    private String getProductUnitPrice(int row) {
        return getSoldProducts().get(row - 1).getProductUnitPrice();
    }

    private String getProductTotalPrice(int row) {
        return getSoldProducts().get(row - 1).getProductTotalPrice();
    }

    private ObservableList<SoldProductViewModel> getSoldProducts() {
        TableView<SoldProductViewModel> tableView = find(SOLD_PRODUCTS_TABLE);
        return tableView.getItems();
    }

    @After
    public void backToRestaurantView() {
        clickButtonThenWait(TO_RESTAURANT, 500);
//        clickOnThenWait(Resources.WAITER.getString("SaleView.ToPaymentView"), 500);
//        clickOnThenWait(Resources.WAITER.getString("PaymentView.Pay"), 500);
//        runInConfigurationMode(() -> deleteTable(TABLE_NUMBER));
    }
}
