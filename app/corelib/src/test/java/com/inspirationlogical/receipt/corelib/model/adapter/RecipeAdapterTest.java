package com.inspirationlogical.receipt.corelib.model.adapter;

import static junit.framework.TestCase.assertEquals;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.AbstractTest;
import org.junit.Before;
import org.junit.Test;

public class RecipeAdapterTest extends AbstractTest {

    private ProductAdapter product;
    private ProductAdapter productFour;

    @Before
    public void setUp() {
        product = new ProductAdapter(schema.getProductOne());
        productFour = new ProductAdapter(schema.getProductFour());
    }

    @Test
    public void testGetRecipesOfProduct() {
        List<RecipeAdapter> recipeAdapters = RecipeAdapter.getRecipesOfProduct(product);
        assertEquals(1, recipeAdapters.size());
    }

    @Test
    public void testGetRecipesOfProductFour() {
        List<RecipeAdapter> recipeAdapters = RecipeAdapter.getRecipesOfProduct(productFour);
        assertEquals(3, recipeAdapters.size());
    }
}
