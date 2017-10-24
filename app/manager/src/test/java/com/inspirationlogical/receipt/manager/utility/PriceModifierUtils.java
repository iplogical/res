package com.inspirationlogical.receipt.manager.utility;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.clickButtonThenWait;

public class PriceModifierUtils {

    public static void backToGoodsView() {
        clickButtonThenWait("Common.GoodsView", 100);
    }

}
