package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;

public class AdHocProductFormControllerTest extends SaleViewTest {

    @Test
    public void testSellAdHocProduct() {
        sellAdHocProduct("TestAdHocProductName", 2, 200, 400);
        assertSoldProduct(1, "TestAdHocProductName", 2, 400, 800);
        selectiveCancellation("TestAdHocProductName");
        verifyThatNotVisible(WaiterResources.WAITER.getString("AdHocProductForm.Title"));
    }

    @Test
    public void testSellAdHocProductInvalidInput() {
        clickButtonThenWait(SELL_ADHOC_PRODUCT, 100);
        verifyThatVisible(WaiterResources.WAITER.getString("AdHocProductForm.Title"));
        setTextField(ADHOC_PRODUCT_NAME, "TestAdHocProductName");
        setTextField(ADHOC_PRODUCT_QUANTITY, "");
        setTextField(ADHOC_PRODUCT_PURCHASE_PRICE, Integer.toString(500));
        setTextField(ADHOC_PRODUCT_SALE_PRICE, Integer.toString(500));
        clickButtonThenWait("Form.Confirm", 100);
        verifyErrorMessage("AdHocProductForm.NumberFormatError");
        sleep(6000);
        clickButtonThenWait("Form.Cancel", 100);
        verifyThatNotVisible(WaiterResources.WAITER.getString("AdHocProductForm.Title"));
    }
}
