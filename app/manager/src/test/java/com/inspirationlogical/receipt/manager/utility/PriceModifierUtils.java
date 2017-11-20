package com.inspirationlogical.receipt.manager.utility;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.MODIFY_PRODUCT;

public class PriceModifierUtils extends AbstractUtils {

    public static void backToGoodsView() {
        clickButtonThenWait("Common.GoodsView", 100);
    }

    public static void clickOnAddPriceModifier() {
        clickButtonThenWait(ADD_PRICE_MODIFIER, 500);
    }

    public static void clickOnModifyPriceModifier() {
        clickButtonThenWait(MODIFY_PRICE_MODIFIER, 500);
    }

    public static void clickOnDeletePriceModifier() {
        clickButtonThenWait(DELETE_PRICE_MODIFIER, 500);
    }
}
