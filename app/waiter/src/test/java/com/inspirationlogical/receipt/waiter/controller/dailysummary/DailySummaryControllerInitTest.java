package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.model.adapter.restaurant.DailyConsumptionAdapter;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterDailySummary;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.assertSoldProduct;

public class DailySummaryControllerInitTest extends TestFXBase {

    private static final int INITIAL_PRODUCT_DISCOUNT = 0;
    private static final int INITIAL_TABLE_DISCOUNT = 400;
    private static final int INITIAL_TOTAL_DISCOUNT = 400;

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
        assertSoldProduct(8, DailyConsumptionAdapter.DiscountType.PRODUCT.toI18nString(), 0, INITIAL_PRODUCT_DISCOUNT, 0);
        assertSoldProduct(9, DailyConsumptionAdapter.DiscountType.TABLE.toI18nString(), 0, INITIAL_TABLE_DISCOUNT, 0);
        assertSoldProduct(10, DailyConsumptionAdapter.DiscountType.TOTAL.toI18nString(), 0, INITIAL_TOTAL_DISCOUNT, 0);
    }

    @After
    public void toRestaurantView() {
        DailySummaryUtils.backToRestaurantView();
    }
}
