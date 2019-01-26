package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import javafx.scene.control.Label;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.RESTAURANT_TEST_TABLE;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.PayUtils.pay;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterSaleView;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.openTable;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.reOpenTable;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static org.junit.Assert.assertEquals;

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
        clickOnGiftProduct();
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 1, 0, 0);
        clickOnGiftProduct();
        selectiveCancellation(PRODUCT_FIVE_LONG + " *");
    }

    @Test
    public void testSellMultipleGiftProductsWithin5Seconds() {
        clickOnGiftProduct();
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        clickOnGiftProduct();
        sellProduct(PRODUCT_FIVE);
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 2, 0, 0);
        selectiveCancellation(PRODUCT_FIVE_LONG + " *");
    }

    @Test
    public void testIncreaseGiftProduct() {
        clickOnGiftProduct();
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        clickOnThenWait(PRODUCT_FIVE_LONG + " *", 20);
        clickOnGiftProduct();
        clickOnThenWait(PRODUCT_FIVE_LONG + " *", 20);
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 3, 0, 0);
        selectiveCancellation(PRODUCT_FIVE_LONG + " *");
    }

    @Test
    public void testSellGiftProductThenNormalProduct() {
        clickOnGiftProduct();
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
        guestPlus();
        assertEquals(TABLE_NUMBER + " (" + (Integer.valueOf(TABLE_GUESTS) + 2) + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        guestMinus();
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
    public void testOrderDeliveredButton() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 3);
        assertEquals("3.0 (3)", getSoldProductQuantityWithRecent(1));
        clickButtonThenWait(ORDER_DELIVERED, 50);
        enterSaleView(TABLE_NUMBER);
        assertEquals("3.0", getSoldProductQuantityWithRecent(1));
        clickOnThenWait(PRODUCT_FIVE_LONG, 50);
        assertEquals("4.0 (1)", getSoldProductQuantityWithRecent(1));
        selectiveCancellation(PRODUCT_FIVE_LONG);
    }

    @Test
    public void testReOpenTable() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 5);
        sellProduct(PRODUCT_TWO, 2);
        sellProduct(PRODUCT_SIX, 6);
        enterPaymentView();
        pay();
        reOpenTable(TABLE_NUMBER);
        enterSaleView(TABLE_NUMBER);
        assertSoldProduct(1, PRODUCT_FIVE_LONG, 5, 440, 2200);
        assertSoldProduct(2, PRODUCT_TWO_LONG, 2, 200, 400);
        assertSoldProduct(3, PRODUCT_SIX_LONG, 6, 480, 2880);
        selectiveCancellation(PRODUCT_FIVE_LONG);
        selectiveCancellation(PRODUCT_TWO_LONG);
        selectiveCancellation(PRODUCT_SIX_LONG);
    }
}
