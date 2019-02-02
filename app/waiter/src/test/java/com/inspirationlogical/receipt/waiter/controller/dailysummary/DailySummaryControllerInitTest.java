package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterDailySummary;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.assertSoldProduct;

@Ignore
public class DailySummaryControllerInitTest extends TestFXBase {

    @Before
    public void toDailySummary() {
        enterDailySummary();
    }

    @Test
    public void testInitialPriceFields() {
        assertSoldTotalPrices(0, 0, 0);
    }

    @Test
    public void testInitialSoldProducts() {
        assertSoldProduct(1, "receiptSaleFourRecordOne *", 2, 800, 1600);
        assertSoldProduct(2, "receiptSaleClosedTableROne", 2, 2000, 4000);
        assertSoldProduct(3, "receiptSaleTwoRecordOne", 1, 1000, 1000);
        assertSoldProduct(4, "receiptSaleTwoRecordTwo", 0.5, 2000, 1000);
        assertTotalCash(0, 0);
        assertTotalCreditCard(0, 0);
        assertTotalCoupon(0, 0);
        assertProductDiscount(0, 0);
        assertTableDiscount(0, 0);
        assertTotalDiscount(0, 0);
    }

    @After
    public void toRestaurantView() {
        DailySummaryUtils.backToRestaurantView();
    }
}
