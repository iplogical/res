package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Before;

import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.enterPriceModifierView;

public class PriceModifierFormControllerTest extends TestFXBase {

    @Before
    public void toPriceModifierView() {
        enterPriceModifierView();
    }
}
