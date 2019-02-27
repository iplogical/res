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
        assertSoldProduct(1, PRODUCT_FIVE_LONG_DISCOUNTED, 1, 0, 0);
        clickOnGiftProduct();
        selectiveCancellation(PRODUCT_FIVE_LONG_DISCOUNTED);
    }

    @Test
    public void testSellGiftProductThenNormalProduct() {
        clickOnGiftProduct();
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        clickButtonThenWait(GIFT_PRODUCT, 200);
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
        assertSoldTotalPrice(0, 0);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        assertSoldTotalPrice(440, 475);
        sellProduct(PRODUCT_FIVE);
        assertSoldTotalPrice(880, 950);
        sellProduct(PRODUCT_SIX);
        assertSoldTotalPrice(1360, 1469);
        sellProduct(PRODUCT_SIX_LONG);
        assertSoldTotalPrice(1840, 1987);
        selectiveCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(960, 1037);
        selectiveCancellation(PRODUCT_SIX_LONG);
        assertSoldTotalPrice(0, 0);
        sellProduct(PRODUCT_FIVE, 3);
        assertSoldTotalPrice(1320, 1426);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(880, 950);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(440, 475);
        singleCancellation(PRODUCT_FIVE_LONG);
        assertSoldTotalPrice(0, 0);
    }

    @Test
    public void testClickOnGuestPlusThenGuestMinusNegativeNotAllowed() {
        guestPlus();
        assertEquals(TABLE_NUMBER + " (" + (Integer.valueOf(TABLE_GUESTS) + 1) + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        guestPlus();
        assertEquals(TABLE_NUMBER + " (" + (Integer.valueOf(TABLE_GUESTS) + 2) + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        guestMinus();
        assertEquals(TABLE_NUMBER + " (" + (Integer.valueOf(TABLE_GUESTS) + 1) + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        guestMinus();
        assertEquals(TABLE_NUMBER + " (" + TABLE_GUESTS + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        guestMinus();
        guestMinus();
        guestMinus();
        assertEquals(TABLE_NUMBER + " (" + TABLE_GUESTS + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
    }

    @Test
    public void testOrderDeliveredButton() {
        selectCategory(AGGREGATE_TOP_TWO);
        selectCategory(AGGREGATE_TOP_ONE);
        sellProduct(PRODUCT_FIVE, 3);

        selectCategory(AGGREGATE_TOP_TWO);
        sellProduct(PRODUCT_EIGHT, 3);

        assertEquals("3.0 (3)", getSoldProductQuantityWithRecent(1));
        assertEquals("3.0 (3)", getSoldProductQuantityWithRecent(2));

        clickButtonThenWait(FOOD_DELIVERED, 50);
        enterSaleView(TABLE_NUMBER);
        assertEquals("3.0", getSoldProductQuantityWithRecent(1));
        assertEquals("3.0 (3)", getSoldProductQuantityWithRecent(2));

        clickButtonThenWait(DRINK_DELIVERED, 50);
        enterSaleView(TABLE_NUMBER);

        assertEquals("3.0", getSoldProductQuantityWithRecent(1));
        assertEquals("3.0", getSoldProductQuantityWithRecent(2));

        clickOnThenWait(PRODUCT_FIVE_LONG, 50);
        clickOnThenWait(PRODUCT_EIGHT_LONG, 50);
        assertEquals("4.0 (1)", getSoldProductQuantityWithRecent(1));
        assertEquals("4.0 (1)", getSoldProductQuantityWithRecent(2));

        clickButtonThenWait(DRINK_DELIVERED, 50);
        enterSaleView(TABLE_NUMBER);
        assertEquals("4.0 (1)", getSoldProductQuantityWithRecent(1));
        assertEquals("4.0", getSoldProductQuantityWithRecent(2));

        clickButtonThenWait(FOOD_DELIVERED, 50);
        enterSaleView(TABLE_NUMBER);
        assertEquals("4.0", getSoldProductQuantityWithRecent(1));
        assertEquals("4.0", getSoldProductQuantityWithRecent(2));

        selectiveCancellation(PRODUCT_FIVE_LONG);
        selectiveCancellation(PRODUCT_EIGHT_LONG);
    }

    @Test
    public void testSellDiscountProduct() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_TWO, 2);
        assertSoldProduct(1, PRODUCT_TWO_LONG, 2, 200, 400);
        sellProduct(PRODUCT_TWO, 1);
        assertSoldProduct(1, PRODUCT_TWO_LONG_DISCOUNTED, 3, 133, 399);
        sellProduct(PRODUCT_TWO, 1);
        assertSoldProduct(1, PRODUCT_TWO_LONG_DISCOUNTED, 3, 133, 399);
        assertSoldProduct(2, PRODUCT_TWO_LONG, 1, 200, 200);
        sellProduct(PRODUCT_TWO, 1);
        assertSoldProduct(1, PRODUCT_TWO_LONG_DISCOUNTED, 3, 133, 399);
        assertSoldProduct(2, PRODUCT_TWO_LONG, 2, 200, 400);
        sellProduct(PRODUCT_TWO, 1);
        assertSoldProduct(1, PRODUCT_TWO_LONG_DISCOUNTED, 6, 133, 798);
        assertNumberOfSoldProducts(1);
        selectiveCancellation(PRODUCT_TWO_LONG_DISCOUNTED);
    }

    @Test
    public void testIncreaseProducts() {
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_TWO, 5);
        assertSoldProduct(1, PRODUCT_TWO_LONG_DISCOUNTED, 3, 133, 399);
        assertSoldProduct(2, PRODUCT_TWO_LONG, 2, 200, 400);
        clickOnThenWait(PRODUCT_TWO_LONG_DISCOUNTED, 20);
        assertSoldProduct(1, PRODUCT_TWO_LONG_DISCOUNTED, 4, 133, 532);
        clickOnThenWait(PRODUCT_TWO_LONG, 500);
        assertSoldProduct(2, PRODUCT_TWO_LONG, 3, 200, 600);
        clickOnThenWait(PRODUCT_TWO_LONG, 20);
        assertSoldProduct(2, PRODUCT_TWO_LONG, 4, 200, 800);
        selectiveCancellation(PRODUCT_TWO_LONG);
        selectiveCancellation(PRODUCT_TWO_LONG_DISCOUNTED);
    }

    @Test
    public void testIncreaseGiftProduct() {
        clickOnGiftProduct();
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE);
        clickOnThenWait(PRODUCT_FIVE_LONG_DISCOUNTED, 20);
        assertSoldProduct(1, PRODUCT_FIVE_LONG_DISCOUNTED, 2, 0, 0);
        clickOnGiftProduct();
        clickOnThenWait(PRODUCT_FIVE_LONG_DISCOUNTED, 20);
        assertSoldProduct(1, PRODUCT_FIVE_LONG_DISCOUNTED, 3, 0, 0);
        selectiveCancellation(PRODUCT_FIVE_LONG_DISCOUNTED);
    }
}
