package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.enterPriceModifierView;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierUtils.backToGoodsView;

public class PriceModifierControllerTest extends TestFXBase {

    @Before
    public void toPriceModifierView() {
        enterPriceModifierView();
    }

    @Test
    public void testAddPriceModifierSimple() {

    }

    @Test
    public void testAddPriceModifierQuantity() {

    }

    @After
    public void toGoodsView() {
        backToGoodsView();
    }
}
