package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.model.adapter.restaurant.DailyConsumptionAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.getLabel;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.DailySummaryUtils.backToRestaurantView;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterDailySummary;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.assertSoldProduct;
import static org.junit.Assert.assertEquals;

public class DailySummaryControllerTest extends TestFXBase {

    private static final int INITIAL_PRODUCT_DISCOUNT = 0;
    private static final int INITIAL_TABLE_DISCOUNT = 400;
    private static final int INITIAL_TOTAL_DISCOUNT = 400;

    @Before
    public void toDailySummary() {
        enterDailySummary();
    }

    @Test
    public void testInitialPriceFields() {
        assertEquals("13100", getLabel(DAILY_SUMMARY_OPEN_CONSUMPTION));
        assertEquals("5600", getLabel(DAILY_SUMMARY_CASH_TOTAL_PRICE));
        assertEquals("2000", getLabel(DAILY_SUMMARY_CREDIT_CARD_TOTAL_PRICE));
        assertEquals("0", getLabel(DAILY_SUMMARY_COUPON_TOTAL_PRICE));
        assertEquals("20700", getLabel(DAILY_SUMMARY_TOTAL_PRICE));
    }

    @Test
    public void testInitialSoldProducts() {
        assertSoldProduct(1, "receiptSaleFourRecordOne *", 2, 800, 1600);
        assertSoldProduct(2, "receiptSaleClosedTableROne", 2, 2000, 4000);
        assertSoldProduct(3, "receiptSaleTwoRecordOne", 1, 1000, 1000);
        assertSoldProduct(4, "receiptSaleTwoRecordTwo", 0.5, 2000, 1000);
        assertSoldProduct(5, PaymentMethod.CASH.toI18nString(), 0, 5600, 0);
        assertSoldProduct(6, PaymentMethod.CREDIT_CARD.toI18nString(), 0, 2000, 0);
        assertSoldProduct(7, PaymentMethod.COUPON.toI18nString(), 0, 0, 0);
        assertSoldProduct(8, DailyConsumptionAdapter.DiscountType.PRODUCT.toI18nString(), 0, INITIAL_PRODUCT_DISCOUNT, 0);
        assertSoldProduct(9, DailyConsumptionAdapter.DiscountType.TABLE.toI18nString(), 0, INITIAL_TABLE_DISCOUNT, 0);
        assertSoldProduct(10, DailyConsumptionAdapter.DiscountType.TOTAL.toI18nString(), 0, INITIAL_TOTAL_DISCOUNT, 0);
    }

    @After
    public void goBackToRestaurantView() {
        backToRestaurantView();
    }

}
