package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.*;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.PRODUCT_PURCHASE_PRICE;
import static com.inspirationlogical.receipt.manager.utility.ProductFormUtils.*;

public class ProductFormControllerTest extends TestFXBase {

    @Test
    public void testAddProduct() {
        clickOnAddProduct();
        addProductWithDefaultParams("New Test Product", "New TP");
        clickOnConfirm();
        selectCategory(LEAF_FIVE_NAME);
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
        setProductType(1);
        setCategory(1);
        setProductStatus(1);
        setQuantityUnit(1);
        clickOnConfirm();
        verifyErrorMessage("Form.NumberFormatException");
    }

    @Test
    public void testModifyProduct() {
        modifyProduct(PRODUCT_ONE_LONG_NAME, "New Name", "NM");
        verifyThatVisible("New Name");
        modifyProduct("New Name", PRODUCT_ONE_LONG_NAME, PRODUCT_ONE_SHORT_NAME);
    }

    @Test
    public void testModifyProductEmptyName() {
        modifyProduct(PRODUCT_ONE_LONG_NAME, "", "NM");
        verifyErrorMessage("Form.EmptyNameOrChoiceBox");
    }

    @Test
    public void testModifyProductInvalidInput() {
        selectCategory(LEAF_ONE_NAME);
        selectProduct(PRODUCT_ONE_LONG_NAME);
        clickOnModifyProduct();
        setTextField(PRODUCT_PURCHASE_PRICE, "a");
        clickOnConfirm();
        verifyErrorMessage("Form.NumberFormatException");
        clickOnCancel();
    }

    @Test
    public void testDeleteProduct() {
        selectCategory(LEAF_ONE_NAME);
        deleteProduct(PRODUCT_ONE_LONG_NAME);
        verifyThatNotVisible(PRODUCT_ONE_LONG_NAME);
        clickOnShowDeleted();
        verifyThatVisible(PRODUCT_ONE_LONG_NAME);
        selectProduct(PRODUCT_ONE_LONG_NAME);
        clickOnModifyProduct();
        setProductStatus(2);    // Active
        clickOnConfirm();
        verifyThatVisible(PRODUCT_ONE_LONG_NAME);
        clickOnShowDeleted();
        verifyThatVisible(PRODUCT_ONE_LONG_NAME);
    }
}
