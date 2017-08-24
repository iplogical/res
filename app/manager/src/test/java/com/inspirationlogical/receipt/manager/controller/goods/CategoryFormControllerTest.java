package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.manager.controller.TestFXBase;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.AGGREGATE_TOP_ONE_NAME;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.PRODUCT_ONE_LONG_NAME;
import static com.inspirationlogical.receipt.manager.utility.CategoryFormUtils.*;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.clickOnConfirm;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.verifyErrorMessage;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.verifyThatVisible;
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
    public void testAddAggregateCategoryNotAllowed() {
        clickOnAddCategory();
        setCategoryName("New Aggregate");
        setParentCategory(1);
        setAggregateCategoryType();
        clickOnConfirm();
        String errorMessage = Resources.CONFIG.getString("ProductCategoryAlreadyHasLeaf");
        verifyThatVisible(errorMessage);
    }

    @Test
    public void testModifyCategory() {
        modifyCategory(AGGREGATE_TOP_ONE_NAME, "New Category Name");
        verifyThatVisible("New Category Name");
        modifyCategory("New Category Name", AGGREGATE_TOP_ONE_NAME);
    }
}
