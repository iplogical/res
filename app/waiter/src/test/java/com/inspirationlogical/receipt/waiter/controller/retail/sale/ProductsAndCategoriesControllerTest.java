package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import com.inspirationlogical.receipt.waiter.utility.ClickUtils;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.SEARCH_FIELD;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TO_RESTAURANT;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterSaleView;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.ENTER;
import static org.junit.Assert.assertEquals;

public class ProductsAndCategoriesControllerTest extends SaleViewTest {

    @Test
    public void testSelectCategory() {
        verifyThatVisible(AGGREGATE_TOP_ONE);
        verifyThatVisible(AGGREGATE_TOP_TWO);

        selectCategory(AGGREGATE_ONE);
        verifyThatVisible(PRODUCT_TWO);
        verifyThatVisible(PRODUCT_FIVE);
        verifyThatVisible(LEAF_ONE);
        verifyThatVisible(LEAF_THREE);
        verifyThatNotVisible(AGGREGATE_TOP_ONE);

        selectCategory(LEAF_ONE);
        verifyThatNotVisible(PRODUCT_TWO);
        verifyThatVisible(PRODUCT_FIVE);
        verifyThatVisible(LEAF_ONE);
        verifyThatVisible(LEAF_THREE);
        verifyThatNotVisible(AGGREGATE_TOP_ONE);

        selectCategory(BACK);
        verifyThatVisible(PRODUCT_FIVE);
        verifyThatVisible(PRODUCT_TWO);
        verifyThatVisible(LEAF_ONE);
        verifyThatVisible(LEAF_THREE);
        verifyThatNotVisible(AGGREGATE_TOP_ONE);

        selectCategory(BACK);
        verifyThatNotVisible(LEAF_ONE);
        verifyThatNotVisible(LEAF_THREE);
        verifyThatVisible(AGGREGATE_TOP_ONE);

        selectCategory(AGGREGATE_TOP_TWO);
        verifyThatVisible(AGGREGATE_TOP_ONE);
        verifyThatVisible(AGGREGATE_TOP_TWO);
        verifyThatVisible(AGGREGATE_FOUR);
        verifyThatNotVisible(AGGREGATE_ONE);

        selectCategory(AGGREGATE_TOP_ONE);
        verifyThatVisible(AGGREGATE_TOP_ONE);
        verifyThatVisible(AGGREGATE_TOP_TWO);
        verifyThatNotVisible(AGGREGATE_FOUR);
        verifyThatVisible(AGGREGATE_ONE);
    }

    @Test
    public void initialCategoryVisibleWhenEnterTheSaleView() {
        verifyThatVisible(AGGREGATE_TOP_ONE);

        selectCategory(AGGREGATE_ONE);
        selectCategory(LEAF_ONE);
        verifyThatNotVisible(AGGREGATE_TOP_ONE);

        clickButtonThenWait(TO_RESTAURANT, 500);
        clickOnThenWait(TABLE_NUMBER, 200);
        verifyThatVisible(AGGREGATE_TOP_ONE);
    }
    @Test
    public void testChildrenCategoriesCleared() {
        selectCategory(AGGREGATE_ONE);
        verifyThatVisible(LEAF_FIVE);
        verifyThatVisible(AGGREGATE_ONE);
        verifyThatVisible(LEAF_ONE);
        verifyThatVisible(LEAF_THREE);

        selectCategory(LEAF_ONE);
        verifyThatVisible(LEAF_FIVE);
        verifyThatVisible(AGGREGATE_ONE);
        verifyThatVisible(LEAF_ONE);
        verifyThatVisible(LEAF_THREE);

        selectCategory(LEAF_THREE);
        verifyThatVisible(LEAF_FIVE);
        verifyThatVisible(AGGREGATE_ONE);
        verifyThatVisible(LEAF_ONE);
        verifyThatVisible(LEAF_THREE);

        selectCategory(LEAF_FIVE);
        verifyThatVisible(LEAF_FIVE);
        verifyThatVisible(AGGREGATE_ONE);
        verifyThatNotVisible(LEAF_ONE);
        verifyThatNotVisible(LEAF_THREE);

        selectCategory(AGGREGATE_ONE);
        verifyThatVisible(LEAF_FIVE);
        verifyThatVisible(AGGREGATE_ONE);
        verifyThatVisible(LEAF_ONE);
        verifyThatVisible(LEAF_THREE);
    }

    @Test
    public void testSearchFieldSellByName() {
        ClickUtils.type(PRODUCT_FIVE);
        type(ENTER, 3);
        assertSoldProduct(1, PRODUCT_FIVE_LONG, 3, 440, 1320);
        selectiveCancellation(PRODUCT_FIVE_LONG);
    }

    @Test
    public void testSearchFieldSellByRapidCode() {
        ClickUtils.type(PRODUCT_FIVE_RAPID_CODE);
        type(ENTER, 3);
        assertSoldProduct(1, PRODUCT_FIVE_LONG, 3, 440, 1320);
        selectiveCancellation(PRODUCT_FIVE_LONG);
    }

    @Test
    public void testSearchFieldClearedWithDelete() {
        ClickUtils.type(PRODUCT_FIVE);
        assertEquals(PRODUCT_FIVE.toLowerCase(), getTextField(SEARCH_FIELD));
        type(DELETE);
        assertEquals("", getTextField(SEARCH_FIELD));
    }

    @Test
    public void testSearchFieldNothingSoldWhenMultipleSelected() {
        ClickUtils.type("prod");
        verifyThatVisible(PRODUCT_FIVE);
        verifyThatVisible(PRODUCT_TWO);
        type(ENTER);
        assertNoSoldProduct();
    }

    @Test
    public void testSearchFieldVisibleProductsUpdatedWhenCleared() {
        ClickUtils.type("ThereIsNoProductMatchThisString");
        verifyThatNotVisible(PRODUCT_FIVE);
        type(DELETE);
        verifyThatVisible(PRODUCT_FIVE);
    }

    @Test
    public void testSearchFieldVisibleProductsUpdatedWhenSearchFieldEmpty() {
        selectCategory(AGGREGATE_ONE);
        ClickUtils.type("N");
        verifyThatNotVisible(PRODUCT_FIVE);
        type(BACK_SPACE);
        verifyThatVisible(PRODUCT_FIVE);
    }

    @Test
    public void testSearchFieldClearedWhenEnterTheSaleView() {
        ClickUtils.type("TestText");
        assertEquals("TestText".toLowerCase(), getTextField(SEARCH_FIELD));
        backToRestaurantView();
        enterSaleView(TABLE_NUMBER);
        assertEquals("", getTextField(SEARCH_FIELD));
    }
}
