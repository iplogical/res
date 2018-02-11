package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.PRODUCT_FIVE_LONG;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.PRODUCT_THREE_LONG;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.closeDay;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterDailySummary;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.assertSoldProduct;

public class DailySummaryControllerPayTest extends TestFXBase {

    @Before
    public void setUpInitialTestState() {
        openTableAndSellProducts();
    }

    @Test
    public void testPayByCash() {
        payByMethod(PaymentMethod.CASH);
        enterDailySummary();
        assertSoldProduct(1, PRODUCT_FIVE_LONG, 5, 440, 2200);
        assertSoldProduct(2, PRODUCT_THREE_LONG, 4, 2900, 11600);   // Total 13800
        assertTotalCash(2, 13800);
        assertSoldTotalPrices(13800, 0, 0);
    }

    @Test
    public void testPayByCreditCard() {
        payByMethod(PaymentMethod.CREDIT_CARD);
        enterDailySummary();
        assertSoldProduct(1, PRODUCT_FIVE_LONG, 5, 440, 2200);
        assertSoldProduct(2, PRODUCT_THREE_LONG, 4, 2900, 11600);   // Total 13800
        assertTotalCreditCard(2, 13800);
        assertSoldTotalPrices(0, 13800, 0);
    }

    @Test
    public void testPayByCoupon() {
        payByMethod(PaymentMethod.COUPON);
        enterDailySummary();
        assertSoldProduct(1, PRODUCT_FIVE_LONG, 5, 440, 2200);
        assertSoldProduct(2, PRODUCT_THREE_LONG, 4, 2900, 11600);   // Total 13800
        assertTotalCoupon(2, 13800);
        assertSoldTotalPrices(0, 0, 13800);
    }
    
    @After
    public void toRestaurantView() {
        DailySummaryUtils.backToRestaurantView();
        closeDay();
    }

}
