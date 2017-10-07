package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.getLabel;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterDailySummary;
import static org.junit.Assert.assertEquals;

public class DailySummaryControllerTest extends TestFXBase {

    @Before
    public void toDailySummary() {
        enterDailySummary();
    }

    @Test
    public void testPriceFields() {
        assertEquals("13100", getLabel(DAILY_SUMMARY_OPEN_CONSUMPTION));
        assertEquals("0", getLabel(DAILY_SUMMARY_CASH_TOTAL_PRICE));
        assertEquals("0", getLabel(DAILY_SUMMARY_CREDIT_CARD_TOTAL_PRICE));
        assertEquals("0", getLabel(DAILY_SUMMARY_COUPON_TOTAL_PRICE));
        assertEquals("8000", getLabel(DAILY_SUMMARY_TOTAL_PRICE));
    }
}
