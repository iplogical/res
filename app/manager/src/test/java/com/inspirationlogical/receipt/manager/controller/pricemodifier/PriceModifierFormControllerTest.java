package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.clickOnConfirm;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.enterPriceModifierView;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierFormUtils.*;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierUtils.clickOnAddPriceModifier;

public class PriceModifierFormControllerTest extends TestFXBase {

    @Before
    public void toPriceModifierView() {
        enterPriceModifierView();
    }

    @Test
    public void testAddPriceModifierForProductSimple() {
        clickOnAddPriceModifier();
        setName("test_PM1");
        setOwnerProduct(1);
        setType(1);
        setQuantityLimit(3);
        setStartDate(LocalDate.now());
        setEndDate(LocalDate.now().plusDays(5));
        setRepeatPeriod(1);
        setDayOfWeek(1);
        setStartTime(LocalTime.of(12, 0));
        setEndTime(LocalTime.of(20, 0));
        clickOnConfirm();
    }
}
