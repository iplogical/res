package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.clickOnConfirm;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.verifyErrorMessage;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.clickOnModifyProduct;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.enterPriceModifierView;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierUtils.*;

public class PriceModifierControllerTest extends TestFXBase {

    @Before
    public void toPriceModifierView() {
        enterPriceModifierView();
    }

    @Test
    public void testAddPriceModifierQuantity() {

    }

    @Test
    public void testModifyPriceModifierNoSelection() {
        clickOnModifyPriceModifier();
        verifyErrorMessage("PriceModifier.SelectPriceModifierForModify");

    }

    @Test
    public void testDeletePriceModifierNoSelection() {
        clickOnDeletePriceModifier();
        verifyErrorMessage("PriceModifier.SelectPriceModifierForDelete");
    }

    @After
    public void toGoodsView() {
        backToGoodsView();
    }
}
