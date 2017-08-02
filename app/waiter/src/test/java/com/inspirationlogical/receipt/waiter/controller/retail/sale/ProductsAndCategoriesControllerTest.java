package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import com.inspirationlogical.receipt.waiter.utility.ClickUtils;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.SEARCH_FIELD;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TO_RESTAURANT;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.ENTER;
import static org.junit.Assert.assertEquals;

public class ProductsAndCategoriesControllerTest extends SaleViewTest {

    @Test
    public void testSelectCategory() {
        verifyThatVisible(DRINKS);
        verifyThatVisible(FOOD);

        selectCategory(BEERS);
        verifyThatVisible(SOPRONI);
        verifyThatVisible(KRUSO);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
        verifyThatNotVisible(DRINKS);

        selectCategory(TAP_BEER);
        verifyThatVisible(SOPRONI);
        verifyThatNotVisible(KRUSO);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
        verifyThatNotVisible(DRINKS);

        selectCategory(BACK);
        verifyThatVisible(SOPRONI);
        verifyThatVisible(KRUSO);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
        verifyThatNotVisible(DRINKS);

        selectCategory(BACK);
        verifyThatNotVisible(TAP_BEER);
        verifyThatNotVisible(BOTTLE_BEER);
        verifyThatVisible(DRINKS);

        selectCategory(FOOD);
        verifyThatVisible(DRINKS);
        verifyThatVisible(FOOD);
        verifyThatVisible(FOODS);
        verifyThatNotVisible(BEERS);

        selectCategory(DRINKS);
        verifyThatVisible(DRINKS);
        verifyThatVisible(FOOD);
        verifyThatNotVisible(FOODS);
        verifyThatVisible(BEERS);
    }

    @Test
    public void initialCategoryVisibleWhenEnterTheSaleView() {
        verifyThatVisible(DRINKS);

        selectCategory(BEERS);
        selectCategory(TAP_BEER);
        verifyThatNotVisible(DRINKS);

        clickButtonThenWait(TO_RESTAURANT, 500);
        clickOnThenWait(TABLE_NUMBER, 200);
        verifyThatVisible(DRINKS);
    }
    @Test
    public void testChildrenCategoriesCleared() {
        selectCategory(BEERS);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);

        selectCategory(TAP_BEER);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);

        selectCategory(BOTTLE_BEER);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);

        selectCategory(COCKTAILS);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatNotVisible(TAP_BEER);
        verifyThatNotVisible(BOTTLE_BEER);

        selectCategory(BEERS);
        verifyThatVisible(COCKTAILS);
        verifyThatVisible(BEERS);
        verifyThatVisible(TAP_BEER);
        verifyThatVisible(BOTTLE_BEER);
    }

    @Test
    public void testSearchFieldSellByName() {
        ClickUtils.type(KRUSO);
        type(ENTER, 3);
        assertEquals(KRUSO_LONG, getProductName(1));
        assertEquals("3.0", getProductQuantity(1));
        assertEquals("480", getProductUnitPrice(1));
        assertEquals("1440", getProductTotalPrice(1));
        selectiveCancellation(KRUSO_LONG);
    }

    @Test
    public void testSearchFieldSellByRapidCode() {
        ClickUtils.type(KRUSO_RAPID_CODE);
        type(ENTER, 3);
        assertEquals(KRUSO_LONG, getProductName(1));
        assertEquals("3.0", getProductQuantity(1));
        assertEquals("480", getProductUnitPrice(1));
        assertEquals("1440", getProductTotalPrice(1));
        selectiveCancellation(KRUSO_LONG);
    }

    @Test
    public void testSearchFieldClearedWithDelete() {
        ClickUtils.type(KRUSO);
        assertEquals(KRUSO.toLowerCase(), getTextField(SEARCH_FIELD));
        type(DELETE);
        assertEquals("", getTextField(SEARCH_FIELD));
    }

    @Test
    public void testSearchFieldNothingSoldWhenMultipleSelected() {
        ClickUtils.type("Sop");
        verifyThatVisible(SOPRONI);
        verifyThatVisible(SOPRONI_SMALL);
        type(ENTER);
        assertSoldProductsEmpty();
    }

    @Test
    public void testSearchFieldVisibleProductsUpdatedWhenCleared() {
        ClickUtils.type("ThereIsNoProductMatchThisString");
        verifyThatNotVisible(SOPRONI);
        type(DELETE);
        verifyThatVisible(SOPRONI);
    }

    @Test
    public void testSearchFieldClearedWhenEnterTheSaleView() {
        ClickUtils.type("TestText");
        assertEquals("TestText".toLowerCase(), getTextField(SEARCH_FIELD));
        clickButtonThenWait(TO_RESTAURANT, 500);
        clickOnThenWait(TABLE_NUMBER, 200);
        assertEquals("", getTextField(SEARCH_FIELD));
    }
}
