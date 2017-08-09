package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import javafx.scene.control.Label;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class SaleControllerTest extends SaleViewTest {

    @Test
    public void testSellProduct() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        assertSoldProductFive(1, 1);
        selectiveCancellation(PRODUCT_FIVE_LONG);
    }


    @Test
    public void testSellGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 1, 0, 0);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectiveCancellation(PRODUCT_FIVE_LONG + " *");
    }

    @Test
    public void testSellMultipleGiftProductsWithin5Seconds() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        sellProduct(PRODUCT_FIVE);
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 2, 0, 0);
        selectiveCancellation(PRODUCT_FIVE_LONG + " *");
    }

    @Test
    public void testIncreaseGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        clickOnThenWait(PRODUCT_FIVE_LONG + " *", 20);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        clickOnThenWait(PRODUCT_FIVE_LONG + " *", 20);
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 3, 0, 0);
        selectiveCancellation(PRODUCT_FIVE_LONG + " *");
    }

    @Test
    public void testSellGiftProductThenNormalProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        clickButtonThenWait(GIFT_PRODUCT, 5100);
        sellProduct(PRODUCT_FIVE);
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 1, 0, 0);
        assertSoldProductFive(2, 1);
        selectiveCancellation(PRODUCT_FIVE_LONG + " *");
        selectiveCancellation(PRODUCT_FIVE_LONG);
    }

    @Test
    public void testSelectiveCancellation() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 5);
        assertSoldProductFive(1, 5);
        selectiveCancellation(PRODUCT_FIVE_LONG);
        assertNoSoldProduct();
    }

    @Test
    public void testSingleCancellation() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 3);
        assertSoldProductFive(1, 3);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldProductFive(1, 2);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldProductFive(1, 1);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertNoSoldProduct();
    }

    @Test
    public void testTableSummaryTotalPrice() {
        assertSoldTotalPrice(0);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        assertSoldTotalPrice(440);
        sellProduct(PRODUCT_FIVE);
        assertSoldTotalPrice(880);
        sellProduct(PRODUCT_SIX);
        assertSoldTotalPrice(1360);
        sellProduct(PRODUCT_SIX_LONG);
        assertSoldTotalPrice(1840);
        selectiveCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(960);
        selectiveCancellation(PRODUCT_SIX_LONG);
        assertSoldTotalPrice(0);
        sellProduct(PRODUCT_FIVE, 3);
        assertSoldTotalPrice(1320);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(880);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(440);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(0);
    }

    @Test
    public void testClickOnGuestPlusThenGuestMinus() {
        guestPlus();
        assertEquals(TABLE_NUMBER + " (" + (Integer.valueOf(TABLE_GUESTS) + 1) + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        guestMinus();
        assertEquals(TABLE_NUMBER + " (" + TABLE_GUESTS + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
    }

    @Test
    public void testClickOnGuestMinusNegativeNotAllowed() {
        guestMinus();
        guestMinus();
        guestMinus();
        assertEquals(TABLE_NUMBER + " (" + TABLE_GUESTS + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
    }

    @Test
    public void testSortByClickTime() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        sellProduct(PRODUCT_TWO);
        sellProduct(PRODUCT_SIX);
        assertEquals(PRODUCT_FIVE_LONG, getSoldProductName(1));
        assertEquals(PRODUCT_TWO_LONG, getSoldProductName(2));
        assertEquals(PRODUCT_SIX_LONG, getSoldProductName(3));

        clickOnThenWait(PRODUCT_FIVE_LONG, 50);
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);
        assertEquals(PRODUCT_TWO_LONG, getSoldProductName(1));
        assertEquals(PRODUCT_SIX_LONG, getSoldProductName(2));
        assertEquals(PRODUCT_FIVE_LONG, getSoldProductName(3));
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);

        clickOnThenWait(PRODUCT_TWO_LONG, 50);
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);
        assertEquals(PRODUCT_SIX_LONG, getSoldProductName(1));
        assertEquals(PRODUCT_FIVE_LONG, getSoldProductName(2));
        assertEquals(PRODUCT_TWO_LONG, getSoldProductName(3));

        selectiveCancellation(PRODUCT_SIX_LONG);
        selectiveCancellation(PRODUCT_FIVE_LONG);
        selectiveCancellation(PRODUCT_TWO_LONG);
    }
}
