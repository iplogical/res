package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.AGGREGATE_TOP_ONE_NAME;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.PRODUCT_ONE_LONG_NAME;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.PRODUCT_ONE_SHORT_NAME;
import static com.inspirationlogical.receipt.manager.utility.CategoryFormUtils.*;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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
        setProductType(1);
        setCategory(1);
        setProductStatus(1);
        setQuantityUnit(1);
        clickOnConfirm();
        verifyErrorMessage("ProductForm.NumberFormatException");
    }

    @Test
    public void testAddProductEmptyChoiceBox() {
        selectProduct(PRODUCT_ONE_LONG_NAME);
        clickOnModifyProduct();
        setTextField(PRODUCT_PURCHASE_PRICE, "a");
        clickOnConfirm();
        verifyErrorMessage("ProductForm.NumberFormatException");
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
        verifyErrorMessage("ProductForm.EmptyLongNameOrChoiceBox");

    }

    @Test
    public void testModifyProductNoSelection() {
        clickOnModifyProduct();
        verifyErrorMessage("ProductForm.SelectProductForModify");
    }

    @Test
    public void testModifyProductCategorySelected() {
        selectCategory(AGGREGATE_TOP_ONE_NAME);
        clickOnModifyProduct();
        verifyErrorMessage("ProductForm.SelectProductForModify");
    }

    @Test
    public void testModifyProductInvalidInput() {
        selectProduct(PRODUCT_ONE_LONG_NAME);
        clickOnModifyProduct();
        setTextField(PRODUCT_PURCHASE_PRICE, "a");
        clickOnConfirm();
        verifyErrorMessage("ProductForm.NumberFormatException");
        clickOnCancel();
    }


    @Test
    public void testDeleteProduct() {
        deleteProduct(PRODUCT_ONE_LONG_NAME);
        verifyThatNotVisible(PRODUCT_ONE_LONG_NAME);
        addProductWithDefaultParams(PRODUCT_ONE_LONG_NAME, PRODUCT_ONE_SHORT_NAME);
    }

    @Test
    public void testDeleteProductNoSelection() {
        clickOnDeleteProduct();
        verifyErrorMessage("ProductForm.SelectProductForDelete");
    }

    @Test
    public void testDeleteProductCategorySelected() {
        selectCategory(AGGREGATE_TOP_ONE_NAME);
        clickOnDeleteProduct();
        verifyErrorMessage("ProductForm.SelectProductForDelete");
    }

    @Test
    public void testAddLeafCategory() {
        clickOnAddCategory();
        setCategoryName("New Leaf");
        setParentCategory(2);
        setLeafCategoryType();
        clickOnConfirm();
        verifyThatVisible("New Leaf");
    }

    @Test
    public void testAddAggregateCategoryNotAllowed() {
        clickOnAddCategory();
        setCategoryName("New Aggregate");
        setParentCategory(1);
        setAggregateCategoryType();
        clickOnConfirm();
        sleep(5000);
        String errorMessage = Resources.CONFIG.getString("ProductCategoryAlreadyHasLeaf");
        verifyThatVisible(errorMessage);
    }

    @Test
    public void testModifyCategory() {
        modifyCategory(AGGREGATE_TOP_ONE_NAME, "New Category Name");
        verifyThatVisible("New Category Name");
        modifyCategory("New Category Name", AGGREGATE_TOP_ONE_NAME);
    }

    @Test
    public void testModifyCategoryNoSelection() {
        clickOnModifyCategory();
        verifyErrorMessage("ProductForm.SelectCategoryForModify");
    }

    @Test
    public void testModifyCategoryProductSelected() {
        selectProduct(PRODUCT_ONE_LONG_NAME);
        clickOnModifyCategory();
        verifyErrorMessage("ProductForm.SelectCategoryForModify");
    }
    @Test
    public void testDeleteCategoryNoSelection() {
        clickOnDeleteCategory();
        verifyErrorMessage("ProductForm.SelectCategoryForDelete");
    }

    @Test
    public void testDeleteCategoryProductSelected() {
        sleep(1000);
        selectProduct(PRODUCT_ONE_LONG_NAME);
        sleep(2000);
        clickOnDeleteCategory();
        verifyErrorMessage("ProductForm.SelectCategoryForDelete");
    }
}

