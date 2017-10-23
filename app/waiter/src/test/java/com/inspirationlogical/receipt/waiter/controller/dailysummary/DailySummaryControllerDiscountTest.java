package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.PayUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterDailySummary;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;

public class DailySummaryControllerDiscountTest extends TestFXBase {

    @Before
    public void setUpInitialTestState() {
        openTableAndSellProducts();
    }

    @Test
    public void testGiftProduct() {
        clickOnGiftProduct();
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_TWO, 3);
        clickOnGiftProduct();
        payByMethod(PaymentMethod.CASH);
        enterDailySummary();
        assertSoldProduct(1, PRODUCT_FIVE_LONG, 5, 440, 2200);
        assertSoldProduct(2, PRODUCT_THREE_LONG, 4, 2900, 11600);   // Total 13800
        assertSoldProduct(3, PRODUCT_TWO_LONG + " *", 3, 0, 0);   // Total 13800
        assertTotalCash(3, 13800);
        assertProductDiscount(3, 600);
        assertTableDiscount(3, 0);
        assertTotalDiscount(3, 600);
        assertSoldTotalPrices(13800, 0, 0);
    }

    @Test
    public void testDiscountPercent() {
        clickButtonThenWait(TO_PAYMENT, 200);
        clickButtonThenWait(PAYMENT_METHOD_CASH, 50);
        clickOnDiscountPercent();
        setDiscountPercent("50");
        pay();
        enterDailySummary();
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 5, 220, 1100);
        assertSoldProduct(2, PRODUCT_THREE_LONG + " *", 4, 1450, 5800);   // Total 13800
        assertTotalCash(2, 6900);
        assertProductDiscount(2, 0);
        assertTableDiscount(2, 6900);
        assertTotalDiscount(2, 6900);
        assertSoldTotalPrices(6900, 0, 0);
    }

    @Test
    public void testDiscountAbsolute() {
        clickButtonThenWait(TO_PAYMENT, 200);
        clickButtonThenWait(PAYMENT_METHOD_CASH, 50);
        clickOnDiscountAbsolute();
        setDiscountAbsolute("6900");
        pay();
        enterDailySummary();
        assertSoldProduct(1, PRODUCT_FIVE_LONG + " *", 5, 220, 1100);
        assertSoldProduct(2, PRODUCT_THREE_LONG + " *", 4, 1450, 5800);   // Total 13800
        assertTotalCash(2, 6900);
        assertProductDiscount(2, 0);
        assertTableDiscount(2, 6900);
        assertTotalDiscount(2, 6900);
        assertSoldTotalPrices(6900, 0, 0);
    }
}
