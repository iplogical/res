package com.inspirationlogical.receipt.waiter.utility;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;

public class DailySummaryUtils {

    public static void backToRestaurantView() {
        clickButtonThenWait("Common.BackToRestaurantView", 1000);
    }
}
