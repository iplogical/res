package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.enterPriceModifierView;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierFormUtils.*;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierUtils.backToGoodsView;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierUtils.clickOnAddPriceModifier;
import static com.inspirationlogical.receipt.manager.utility.PriceModifierUtils.clickOnDeletePriceModifier;

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
        setType(1);     // SIMPLE_DISCOUNT
        setDiscountPercent(33.333);
        setStartDate(LocalDate.now());
        setEndDate(LocalDate.now().plusDays(5));
        setRepeatPeriod(1);     // WEEKLY
        setDayOfWeek(1);
        clickOnConfirm();
        verifyThatVisible("test_PM1");
        clickOn("test_PM1");
        clickOnDeletePriceModifier();
        verifyThatNotVisible("test_PM1");
    }

    @Test
    public void testAddPriceModifierForProductQuantityDaily() {
        clickOnAddPriceModifier();
        setName("test_PM2");
        setOwnerProduct(2);
        setType(2);     // QUANTITY_DISCOUNT
        setQuantityLimit(3);
        setDiscountPercent(33.333);
        setStartDate(LocalDate.now());
        setEndDate(LocalDate.now().plusDays(5));
        setRepeatPeriod(2);     // DAILY
        setStartTime(LocalTime.of(12, 0));
        setEndTime(LocalTime.of(20, 0));
        clickOnConfirm();
        verifyThatVisible("test_PM2");
        clickOn("test_PM2");
        clickOnDeletePriceModifier();
        verifyThatNotVisible("test_PM2");
    }

    @Test
    public void testAddPriceModifierErrorMessages() {
        clickOnAddPriceModifier();
        clickOnConfirm();
        verifyErrorMessage(
                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyName") + System.lineSeparator() +
                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyType") + System.lineSeparator() +
                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyProduct") + System.lineSeparator() +
                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorDiscountFormat") + System.lineSeparator() +
                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyStartDate") + System.lineSeparator() +
                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyEndDate") + System.lineSeparator() +
                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyRepeatPeriod") + System.lineSeparator()
            );
    }

    @After
    public void toGoodsView() {
        backToGoodsView();
    }

}
