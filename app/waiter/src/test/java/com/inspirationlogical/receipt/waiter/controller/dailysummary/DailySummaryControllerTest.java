package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.Before;

import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterDailySummary;

public class DailySummaryControllerTest extends TestFXBase {

    @Before
    public void toDailySummary() {
        enterDailySummary();
    }

    
}
