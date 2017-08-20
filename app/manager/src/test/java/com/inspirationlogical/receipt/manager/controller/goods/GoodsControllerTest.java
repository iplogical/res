package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Test;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;

public class GoodsControllerTest extends TestFXBase {

    @Test
    public void testAddProduct() {
        clickOnAddProduct();
        addProductWithDefaultParams("New Test Product", "New TP");
        clickOnConfirm();
        verifyThatVisible("New Test Product");
    }

    @Test
    public void testAddProductNameAlreadyUsed() {
        String productName = "productFive";
        clickOnAddProduct();
        addProductWithDefaultParams(productName, "product5");
        clickOnConfirm();
        String errorMessage = Resources.CONFIG.getString("ProductNameAlreadyUsed");
        verifyThatVisible(errorMessage + productName);
    }

    @Test
    public void testAddProductInvalidInput() {
        clickOnAddProduct();
        setLongName("New Test Product");
        setShortName("New TP");
        clickOnConfirm();
        verifyErrorMessage("ProductForm.NumberFormatException");
        sleep(3000);
    }
}
