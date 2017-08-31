package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Test;

import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.clickOnShowRecipeForm;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.selectProduct;
import static com.inspirationlogical.receipt.manager.utility.RecipeFormUtils.assertComponent;
import static com.inspirationlogical.receipt.manager.utility.RecipeFormUtils.clickOnClose;

public class RecipeFormControllerTest extends TestFXBase {

    @Test
    public void testComponentsOfSelectedProductsLoaded() {
        selectProduct("productFour");
        clickOnShowRecipeForm();
        assertComponent("productRecipeElementOne");
        assertComponent("productRecipeElementTwo");
        assertComponent("productRecipeElementThree");
        clickOnClose();
    }

    @Test
    public void testAddComponent() {
        selectProduct("productFour");
        clickOnShowRecipeForm();
    }

    @Test
    public void testAddComponentEmptyChoiceBox() {
        selectProduct("productFour");
        clickOnShowRecipeForm();
    }

    @Test
    public void testAddComponentInvalidInput() {
        selectProduct("productFour");
        clickOnShowRecipeForm();
    }

    @Test
    public void testModifyComponent() {

    }

    @Test
    public void testModifyComponentInvalidInput() {

    }

    @Test
    public void testDeleteComponent() {

    }

    @Test
    public void testDeleteComponentNoSelection() {

    }

}
