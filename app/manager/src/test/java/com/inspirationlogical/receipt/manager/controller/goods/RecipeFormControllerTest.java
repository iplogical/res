package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import com.inspirationlogical.receipt.manager.utility.ClickUtils;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.setTextField;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.verifyErrorMessage;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.verifyThatVisible;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.clickOnShowRecipeForm;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.selectProduct;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.RECIPE_FORM_QUANTITY;
import static com.inspirationlogical.receipt.manager.utility.RecipeFormUtils.*;
import static com.inspirationlogical.receipt.manager.utility.RecipeFormUtils.selectProduct;

public class RecipeFormControllerTest extends TestFXBase {

    @Before
    public void showRecipeForm() {
        clickOnShowRecipeForm();
    }

    @Test
    public void testComponentsOfSelectedProductsLoaded() {
        clickOnClose();
        selectProduct("productFour");
        clickOnShowRecipeForm();
        assertComponent("productRecipeElementOne");
        assertComponent("productRecipeElementTwo");
        assertComponent("productRecipeElementThree");
    }

    @Test
    public void testAddComponent() {
        selectProduct(1);
        selectComponent(2);
        setQuantity(0.02);
        addComponent();
        assertComponent("productTwo");
        clickOnClose();
        selectProduct("productOne");
        clickOnShowRecipeForm();
        assertComponent("productTwo");
        clickOnComponent(2);
        deleteComponent();
    }

    @Test
    public void testAddComponentAlreadyAdded() {
        selectProduct(1);
        selectComponent(1);
        setQuantity(2D);
        addComponent();
        verifyErrorMessage("RecipeForm.ComponentAlreadyAdded");
    }

    @Test
    public void testAddComponentEmptyChoiceBox() {
        selectProduct(1);
        setQuantity(2D);
        addComponent();
        verifyErrorMessage("RecipeForm.EmptyChoiceBox");
    }

    @Test
    public void testAddComponentInvalidInput() {
        selectProduct(1);
        selectComponent(2);
        setTextField(RECIPE_FORM_QUANTITY, "Invalid");
        addComponent();
        verifyErrorMessage("RecipeForm.NumberFormatQuantity");
    }

    @Test
    public void testModifyComponent() {
        selectProduct(1);
        editQuantity(1, 2.3);
        type(KeyCode.ENTER);
        clickOnClose();
        showRecipeForm();
        selectProduct(1);
        assertComponent("productOne", 2.3);
        editQuantity(1, 4);
        type(KeyCode.ENTER);


    }

    @Test
    public void testModifyComponentInvalidInput() {
        selectProduct(1);
        doubleClickOn(new Point2D(720, 270));
        write("Invalid");
        type(KeyCode.ENTER);
        verifyErrorMessage("RecipeForm.NumberFormatQuantity");
    }

    @Test
    public void testDeleteComponent() {
        clickOnClose();
        selectProduct("productFour");
        clickOnShowRecipeForm();
        clickOnComponent(1);
        deleteComponent();
        assertNoComponent("productRecipeElementOne");
        clickOnClose();
        selectProduct("productFour");
        clickOnShowRecipeForm();
        assertNoComponent("productRecipeElementOne");
        assertComponent("productRecipeElementTwo");
        assertComponent("productRecipeElementThree");
        selectComponent(8);
        setQuantity(0.2);
        addComponent();
        assertComponent("productRecipeElementOne");
    }

    @Test
    public void testDeleteLastComponentNotAllowed() {
        selectProduct(1);
        clickOnComponent(1);
        deleteComponent();
        verifyErrorMessage("RecipeForm.DeleteLastComponent");
    }

    @Test
    public void testDeleteComponentNoSelection() {
        deleteComponent();
        verifyErrorMessage("RecipeForm.SelectComponentForDelete");
    }

    @After
    public void closeRecipeForm() {
        clickOnClose();
    }
}
