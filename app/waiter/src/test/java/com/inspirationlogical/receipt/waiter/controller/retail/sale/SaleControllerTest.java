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
    public void testClickOnGuestPlusThenGuestMinus() {
        clickOnThenWait(GUEST_PLUS, 100);
        assertEquals(TABLE_NUMBER + " (" + (Integer.valueOf(TABLE_GUESTS) + 1) + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
        clickOnThenWait(GUEST_MINUS, 100);
        assertEquals(TABLE_NUMBER + " (" + TABLE_GUESTS + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
    }

    @Test
    public void testClickOnGuestMinusNegativeNotAllowed() {
        clickOnThenWait(GUEST_MINUS, 100);
        clickOnThenWait(GUEST_MINUS, 100);
        clickOnThenWait(GUEST_MINUS, 100);
        assertEquals(TABLE_NUMBER + " (" + TABLE_GUESTS + "/" + TABLE_CAPACITY + ")", ((Label)find(SALE_TABLE_NUMBER)).getText());
    }

    @Test
    public void testTableSummaryTotalPrice() {
        assertEquals("0 Ft", getLabel(TOTAL_PRICE));
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        assertEquals("440 Ft", getLabel(TOTAL_PRICE));
        sellProduct(SOPRONI);
        assertEquals("880 Ft", getLabel(TOTAL_PRICE));
        sellProduct(KRUSO);
        assertEquals("1360 Ft", getLabel(TOTAL_PRICE));
        clickOnThenWait(KRUSO_LONG, 50);
        assertEquals("1840 Ft", getLabel(TOTAL_PRICE));
        selectiveCancellation(SOPRONI_LONG);
        assertEquals("960 Ft", getLabel(TOTAL_PRICE));
        selectiveCancellation(KRUSO_LONG);
        assertEquals("0 Ft", getLabel(TOTAL_PRICE));
        sellProduct(SOPRONI, 3);
        assertEquals("1320 Ft", getLabel(TOTAL_PRICE));
        singleCancellation(SOPRONI_LONG);
        assertEquals("880 Ft", getLabel(TOTAL_PRICE));
        singleCancellation(SOPRONI_LONG);
        assertEquals("440 Ft", getLabel(TOTAL_PRICE));
        singleCancellation(SOPRONI_LONG);
        assertEquals("0 Ft", getLabel(TOTAL_PRICE));
    }

    @Test
    public void testSortByClickTime() {
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        sellProduct(KRUSO);
        sellProduct(EDELWEISS);
        assertEquals(SOPRONI_LONG, getProductName(1));
        assertEquals(KRUSO_LONG, getProductName(2));
        assertEquals(EDELWEISS_LONG, getProductName(3));

        clickOnThenWait(SOPRONI_LONG, 50);
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);
        assertEquals(KRUSO_LONG, getProductName(1));
        assertEquals(EDELWEISS_LONG, getProductName(2));
        assertEquals(SOPRONI_LONG, getProductName(3));
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);

        clickOnThenWait(KRUSO_LONG, 50);
        clickButtonThenWait(SORT_BY_CLICK_TIME, 50);
        assertEquals(EDELWEISS_LONG, getProductName(1));
        assertEquals(SOPRONI_LONG, getProductName(2));
        assertEquals(KRUSO_LONG, getProductName(3));

        selectiveCancellation(EDELWEISS_LONG);
        selectiveCancellation(SOPRONI_LONG);
        selectiveCancellation(KRUSO_LONG);
    }

    @Test
    public void testSellProduct() {
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        assertEquals(SOPRONI_LONG, getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("440", getProductTotalPrice(1));
        selectiveCancellation(SOPRONI_LONG);
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
        assertEquals(SOPRONI_LONG + " *", getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
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
        assertEquals(SOPRONI_LONG + " *", getProductName(1));
        assertEquals("2.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
        selectiveCancellation(SOPRONI_LONG + " *");
    }

    @Test
    public void testSellGiftProductThenNormalProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        clickButtonThenWait(GIFT_PRODUCT, 5100);
        sellProduct(SOPRONI);
        assertEquals(SOPRONI_LONG + " *", getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
        assertEquals(SOPRONI_LONG, getProductName(2));
        assertEquals("1.0", getProductQuantity(2));
        assertEquals("440", getProductUnitPrice(2));
        assertEquals("440", getProductTotalPrice(2));
        selectiveCancellation(SOPRONI_LONG + " *");
        selectiveCancellation(SOPRONI_LONG);
    }

    @Test
    public void testSelectiveCancellation() {
        selectCategory(BEERS);
        sellProduct(SOPRONI, 5);
        assertEquals(SOPRONI_LONG, getProductName(1));
        assertEquals("5.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("2200", getProductTotalPrice(1));

        selectiveCancellation(SOPRONI_LONG);
        assertSoldProductsEmpty();
    }

    @Test
    public void testSingleCancellation() {
        selectCategory(BEERS);
        sellProduct(SOPRONI, 3);
        assertEquals(SOPRONI_LONG, getProductName(1));
        assertEquals("3.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("1320", getProductTotalPrice(1));

        singleCancellation(SOPRONI_LONG);
        assertEquals(SOPRONI_LONG, getProductName(1));
        assertEquals("2.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("880", getProductTotalPrice(1));

        singleCancellation(SOPRONI_LONG);
        assertEquals(SOPRONI_LONG, getProductName(1));
        assertEquals("1.0", getProductQuantity(1));
        assertEquals("440", getProductUnitPrice(1));
        assertEquals("440", getProductTotalPrice(1));

        singleCancellation(SOPRONI_LONG);
        assertSoldProductsEmpty();
    }

    @Test
    public void testIncreaseGiftProduct() {
        clickButtonThenWait(GIFT_PRODUCT, 20);
        selectCategory(BEERS);
        sellProduct(SOPRONI);
        clickOnThenWait(SOPRONI_LONG + " *", 20);
        clickButtonThenWait(GIFT_PRODUCT, 20);
        clickOnThenWait(SOPRONI_LONG + " *", 20);
        assertEquals(SOPRONI_LONG + " *", getProductName(1));
        assertEquals("3.0", getProductQuantity(1));
        assertEquals("0", getProductUnitPrice(1));
        assertEquals("0", getProductTotalPrice(1));
        selectiveCancellation(SOPRONI_LONG + " *");
    }
}
