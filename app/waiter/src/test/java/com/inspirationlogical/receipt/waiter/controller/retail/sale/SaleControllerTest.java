package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import com.inspirationlogical.receipt.corelib.utility.Resources;
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
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        assertSoldSoproni(1, 1);
        selectiveCancellation(SOPRONI_LONG);
    }


    @Test
    public void testSellGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        assertSoldProduct(1, SOPRONI_LONG + " *", 1, 0, 0);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectiveCancellation(SOPRONI_LONG + " *");
    }

    @Test
    public void testSellMultipleGiftProductsWithin5Seconds() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        sellProduct(SOPRONI);
        assertSoldProduct(1, SOPRONI_LONG + " *", 2, 0, 0);
        selectiveCancellation(SOPRONI_LONG + " *");
    }

    @Test
    public void testIncreaseGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        clickOnThenWait(SOPRONI_LONG + " *", 20);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        clickOnThenWait(SOPRONI_LONG + " *", 20);
        assertSoldProduct(1, SOPRONI_LONG + " *", 3, 0, 0);
        selectiveCancellation(SOPRONI_LONG + " *");
    }

    @Test
    public void testSellGiftProductThenNormalProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        clickButtonThenWait(GIFT_PRODUCT, 5100);
        sellProduct(SOPRONI);
        assertSoldProduct(1, SOPRONI_LONG + " *", 1, 0, 0);
        assertSoldSoproni(2, 1);
        selectiveCancellation(SOPRONI_LONG + " *");
        selectiveCancellation(SOPRONI_LONG);
    }

    @Test
    public void testSelectiveCancellation() {
        selectCategory(BEERS);
        sellProduct(SOPRONI, 5);
        assertSoldSoproni(1, 5);
        selectiveCancellation(SOPRONI_LONG);
        assertNoSoldProduct();
    }

    @Test
    public void testSingleCancellation() {
        selectCategory(BEERS);
        sellProduct(SOPRONI, 3);
        assertSoldSoproni(1, 3);
        singleCancellation(SOPRONI_LONG);
        assertSoldSoproni(1, 2);
        singleCancellation(SOPRONI_LONG);
        assertSoldSoproni(1, 1);
        singleCancellation(SOPRONI_LONG);
        assertNoSoldProduct();
    }

    @Test
    public void testTableSummaryTotalPrice() {
        assertSoldTotalPrice(0);
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        assertSoldTotalPrice(440);
        sellProduct(SOPRONI);
        assertSoldTotalPrice(880);
        sellProduct(KRUSO);
        assertSoldTotalPrice(1360);
        sellProduct(KRUSO_LONG);
        assertSoldTotalPrice(1840);
        selectiveCancellation(SOPRONI_LONG);
        assertSoldTotalPrice(960);
        selectiveCancellation(KRUSO_LONG);
        assertSoldTotalPrice(0);
        sellProduct(SOPRONI, 3);
        assertSoldTotalPrice(1320);
        singleCancellation(SOPRONI_LONG);
        assertSoldTotalPrice(880);
        singleCancellation(SOPRONI_LONG);
        assertSoldTotalPrice(440);
        singleCancellation(SOPRONI_LONG);
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
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        sellProduct(KRUSO);
        sellProduct(EDELWEISS);
        assertEquals(SOPRONI_LONG, getSoldProductName(1));
        assertEquals(KRUSO_LONG, getSoldProductName(2));
        assertEquals(EDELWEISS_LONG, getSoldProductName(3));

        clickOnThenWait(SOPRONI_LONG, 50);
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);
        assertEquals(KRUSO_LONG, getSoldProductName(1));
        assertEquals(EDELWEISS_LONG, getSoldProductName(2));
        assertEquals(SOPRONI_LONG, getSoldProductName(3));
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);

        clickOnThenWait(KRUSO_LONG, 50);
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);
        assertEquals(EDELWEISS_LONG, getSoldProductName(1));
        assertEquals(SOPRONI_LONG, getSoldProductName(2));
        assertEquals(KRUSO_LONG, getSoldProductName(3));

        selectiveCancellation(EDELWEISS_LONG);
        selectiveCancellation(SOPRONI_LONG);
        selectiveCancellation(KRUSO_LONG);
    }
}
