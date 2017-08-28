package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.*;
import static com.inspirationlogical.receipt.manager.utility.CategoryFormUtils.*;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.*;
import static com.inspirationlogical.receipt.manager.utility.GoodsUtils.clickOnDeleteCategory;

public class CategoryFormControllerTest extends TestFXBase {

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
    public void testAddCategoryNameAlreadyUsed() {
        clickOnAddCategory();
        setCategoryName("leafOne");
        setParentCategory(2);
        setLeafCategoryType();
        clickOnConfirm();
        String errorMessage = Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed");
        verifyThatVisible(errorMessage + "leafOne");
    }

    @Test
    public void testAddCategoryInvalidInput() {
        clickOnAddCategory();
        setCategoryName("leafOne");
        setCategoryOrderNumber("a");
        setParentCategory(2);
        setLeafCategoryType();
        clickOnConfirm();
        verifyErrorMessage("Form.NumberFormatException");
    }

    @Test
    public void testAddCategoryEmptyChoiceBox() {
        clickOnAddCategory();
        setCategoryName("leafOne");
        setLeafCategoryType();
        clickOnConfirm();
        verifyErrorMessage("Form.EmptyNameOrChoiceBox");
    }

    @Test
    public void testAddCategoryEmptyName() {
        clickOnAddCategory();
        setParentCategory(2);
        setLeafCategoryType();
        clickOnConfirm();
        verifyErrorMessage("Form.EmptyNameOrChoiceBox");
    }

    @Test
    public void testModifyCategory() {
        modifyCategory(AGGREGATE_TOP_ONE_NAME, "New Category Name");
        verifyThatVisible("New Category Name");
        modifyCategory("New Category Name", AGGREGATE_TOP_ONE_NAME);
        verifyThatVisible(AGGREGATE_TOP_ONE_NAME);
    }

    @Test
    public void testAddAggregateThenLeaf() {
        clickOnAddCategory();
        setCategoryName("AggregateA");
        setParentCategory(5);
        setAggregateCategoryType();
        clickOnConfirm();
        verifyThatVisible("AggregateA");
        clickOnAddCategory();
        setCategoryName("LeafA");
        setParentCategory(1);
        setAggregateCategoryType();
        clickOnConfirm();
        verifyThatVisible("AggregateA");
        verifyThatVisible("LeafA");
    }

    @Test
    public void testDeleteAggregateCategory() {
        selectCategory(AGGREGATE_TOP_ONE_NAME);
        clickOnDeleteCategory();
        verifyThatNotVisible(AGGREGATE_TOP_ONE_NAME);
        verifyThatNotVisible(AGGREGATE_ONE_NAME);
        verifyThatNotVisible(LEAF_ONE_NAME);
        verifyThatNotVisible(PRODUCT_ONE_LONG_NAME);
        clickOnShowDeleted();
        verifyThatVisible(AGGREGATE_TOP_ONE_NAME);
        verifyThatVisible(AGGREGATE_ONE_NAME);
        verifyThatVisible(LEAF_ONE_NAME);
        verifyThatVisible(PRODUCT_ONE_LONG_NAME);
        selectCategory(AGGREGATE_TOP_ONE_NAME);
        clickOnModifyCategory();
        setCategoryStatus(2); // Active
        clickOnConfirm();
        clickOnShowDeleted();
        verifyThatVisible(AGGREGATE_TOP_ONE_NAME);
        verifyThatVisible(AGGREGATE_ONE_NAME);
        verifyThatVisible(LEAF_ONE_NAME);
        verifyThatVisible(PRODUCT_ONE_LONG_NAME);
    }

    @Test
    public void testDeleteLeafCategory() {
        selectCategory(LEAF_ONE_NAME);
        clickOnDeleteCategory();
        verifyThatNotVisible(LEAF_ONE_NAME);
        verifyThatNotVisible(PRODUCT_ONE_LONG_NAME);
        clickOnShowDeleted();
        verifyThatVisible(LEAF_ONE_NAME);
        verifyThatVisible(PRODUCT_ONE_LONG_NAME);
        selectCategory(LEAF_ONE_NAME);
        clickOnModifyCategory();
        setCategoryStatus(2); // Active
        clickOnConfirm();
        clickOnShowDeleted();
        verifyThatVisible(LEAF_ONE_NAME);
        verifyThatVisible(PRODUCT_ONE_LONG_NAME);
    }
}
