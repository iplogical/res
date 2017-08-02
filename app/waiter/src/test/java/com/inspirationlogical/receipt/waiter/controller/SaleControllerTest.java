package com.inspirationlogical.receipt.waiter.controller;

import javafx.scene.control.Label;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
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
        verifyThatVisible(DRINKS);
        verifyThatVisible(FOOD);

        selectCategory(BEERS);
        verifyThatVisible(SOPRONI);
        verifyThatVisible(KRUSO);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
        verifyThatNotVisible(DRINKS);

        selectCategory(TAP_BEER);
        verifyThatVisible(SOPRONI);
        verifyThatNotVisible(KRUSO);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
        verifyThatNotVisible(DRINKS);

        selectCategory(BACK);
        verifyThatVisible(SOPRONI);
        verifyThatVisible(KRUSO);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
        verifyThatNotVisible(DRINKS);

        selectCategory(BACK);
        verifyThatNotVisible(TAP_BEER);
        verifyThatNotVisible(BOTTLE_BEER);
        verifyThatVisible(DRINKS);

        selectCategory(FOOD);
        verifyThatVisible(DRINKS);
        verifyThatVisible(FOOD);
        verifyThatVisible(FOODS);
        verifyThatNotVisible(BEERS);

        selectCategory(DRINKS);
        verifyThatVisible(DRINKS);
        verifyThatVisible(FOOD);
        verifyThatNotVisible(FOODS);
        verifyThatVisible(BEERS);
    }

    @Test
    public void initialCategoryVisibleWhenEnterTheSaleView() {
        verifyThatVisible(DRINKS);

        selectCategory(BEERS);
        selectCategory(TAP_BEER);
        verifyThatNotVisible(DRINKS);

        clickButtonThenWait(TO_RESTAURANT, 500);
        clickOnThenWait(TABLE_NUMBER, 200);
        verifyThatVisible(DRINKS);
    }
    @Test
    public void testChildrenCategoriesCleared() {
        selectCategory(BEERS);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);

        selectCategory(TAP_BEER);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);

        selectCategory(BOTTLE_BEER);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);

        selectCategory(COCKTAILS);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatNotVisible(TAP_BEER);
        verifyThatNotVisible(BOTTLE_BEER);

        selectCategory(BEERS);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
    }

    @Test
    public void testSellProduct() {
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        assertEquals("Soproni 0,5L", getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("440", getProductTotalPrice(1));
        selectiveCancellation("Soproni 0,5L");
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
        selectCategory(BEERS);
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
        selectCategory(BEERS);
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
        selectCategory(BEERS);
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
        selectCategory(BEERS);
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
        selectCategory(BEERS);
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
        selectCategory(BEERS);
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

    @After
    public void backToRestaurantView() {
        clickButtonThenWait(TO_RESTAURANT, 500);
//        clickOnThenWait(Resources.WAITER.getString("SaleView.ToPaymentView"), 500);
//        clickOnThenWait(Resources.WAITER.getString("PaymentView.Pay"), 500);
//        runInConfigurationMode(() -> deleteTable(TABLE_NUMBER));
    }
}
