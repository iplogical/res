package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.AGGREGATE_TOP_ONE_NAME;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.PRODUCT_ONE_LONG_NAME;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.PRODUCT_ONE_SHORT_NAME;
import static com.inspirationlogical.receipt.manager.utility.CategoryFormUtils.*;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;

public class GoodsControllerTest extends TestFXBase {

    @Ignore
    @Test
    public void launchAppWithTestDataBase() {
        while (true) {}
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
        selectProduct(PRODUCT_ONE_LONG_NAME);
        clickOnDeleteCategory();
        verifyErrorMessage("ProductForm.SelectCategoryForDelete");
    }

    @After
    public void clearSelections() {
        clickButtonThenWait(CREATE_RECIPE, 200);
        clickButtonThenWait(HIDE, 200);
    }
}

