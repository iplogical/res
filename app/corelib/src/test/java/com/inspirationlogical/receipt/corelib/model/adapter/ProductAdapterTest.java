package com.inspirationlogical.receipt.corelib.model.adapter;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bálint on 2017.03.20..
 */
public class ProductAdapterTest {

    private ProductAdapter productFour;
    private RecipeParams recipeParamOne;
    private RecipeParams recipeParamTwo;
    private RecipeParams recipeParamThree;
    private List<RecipeParams> recipeParams;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        productFour = new ProductAdapter(schema.getProductFour());
        recipeParamOne = RecipeParams.builder().componentName("productRecipeElementOne").quantity(0.2).build();
        recipeParamTwo = RecipeParams.builder().componentName("productRecipeElementTwo").quantity(0.05).build();
        recipeParamThree = RecipeParams.builder().componentName("productRecipeElementThree").quantity(0.1).build();
        recipeParams = new ArrayList<>(Arrays.asList(recipeParamOne, recipeParamTwo, recipeParamThree));
    }

    @Test
    public void testGetAdHocProduct() {
        ProductAdapter adHocProduct = ProductAdapter.getAdHocProduct();
        assertEquals(ProductType.AD_HOC_PRODUCT, adHocProduct.getAdaptee().getType());
    }

    @Test
    public void testGetProductByName() {
        assertEquals(1, ProductAdapter.getProductByName("product").size());
    }

    @Test
    public void testUpdateRecipes() {
        recipeParams.get(0).setQuantity(1);
        productFour.updateRecipes(recipeParams);
        Product updatedProductFour = ProductAdapter.getProductByName("productFour").get(0);
        assertEquals(1, updatedProductFour.getRecipes().stream()
                .filter(recipe -> recipe.getComponent().getLongName().equals("productRecipeElementOne"))
                .collect(toList()).get(0).getQuantityMultiplier(), 0.001);
        assertEquals(3, updatedProductFour.getRecipes().size());
    }

    @Test
    public void testAddRecipes() {
        recipeParams.add(RecipeParams.builder().componentName("productTwo").quantity(1).build());
        productFour.addRecipes(recipeParams);
        Product updatedProductFour = ProductAdapter.getProductByName("productFour").get(0);
        assertEquals(1, updatedProductFour.getRecipes().stream()
                .filter(recipe -> recipe.getComponent().getLongName().equals("productTwo"))
                .collect(toList()).get(0).getQuantityMultiplier(), 0.001);
        assertEquals(4, updatedProductFour.getRecipes().size());
    }

    @Test
    public void testDeleteRecipes() {
        recipeParams.remove(recipeParamOne);
        productFour.deleteRecipes(recipeParams);
        Product updatedProductFour = ProductAdapter.getProductByName("productFour").get(0);
        assertEquals(0, updatedProductFour.getRecipes().stream()
                .filter(recipe -> recipe.getComponent().getLongName().equals("productRecipeElementOne"))
                .collect(toList()).size(), 0.001);
        assertEquals(2, updatedProductFour.getRecipes().size());
    }

}
