package com.inspirationlogical.receipt.corelib.model.adapter;

import static junit.framework.TestCase.assertEquals;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.Product;

public class RecipeAdapterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testGetRecipeOfProduct() {
        Product product = ProductCategoryAdapter.getRootCategory().getAllSellableProducts()
                .stream()
                .map(AbstractAdapter::getAdaptee)
                .filter(p -> p.getLongName().equals("productTwo"))
                .findFirst().orElse(null);
        RecipeAdapter recipeAdapter = RecipeAdapter.getRecipeOfProduct(schema.getEntityManager(), product);
        assertEquals(0.5, recipeAdapter.getAdaptee().getQuantityMultiplier());
    }

    @Test
    public void testGetRecipesOfProduct() {
        Product product = ProductCategoryAdapter.getRootCategory().getAllSellableProducts()
                .stream()
                .map(AbstractAdapter::getAdaptee)
                .filter(p -> p.getLongName().equals("productFour"))
                .findFirst().orElse(null);
        List<RecipeAdapter> recipeAdapters = RecipeAdapter.getRecipesOfProduct(schema.getEntityManager(), product);
        assertEquals(3, recipeAdapters.size());
    }

}
