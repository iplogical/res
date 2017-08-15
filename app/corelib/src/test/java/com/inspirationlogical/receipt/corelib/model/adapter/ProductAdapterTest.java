package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_PRODUCTS;
import static com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter.getProductById;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Bálint on 2017.03.20..
 */
public class ProductAdapterTest extends TestBase {

    private ProductAdapter productFour;
    private RecipeParams recipeParamOne;
    private RecipeParams recipeParamTwo;
    private RecipeParams recipeParamThree;
    private List<RecipeParams> recipeParams;
    private Product.ProductBuilder builder;

    @Before
    public void setUp() {
        productFour = new ProductAdapter(schema.getProductFour());
        recipeParamOne = RecipeParams.builder().componentName("productRecipeElementOne").quantity(0.2).build();
        recipeParamTwo = RecipeParams.builder().componentName("productRecipeElementTwo").quantity(0.05).build();
        recipeParamThree = RecipeParams.builder().componentName("productRecipeElementThree").quantity(0.1).build();
        recipeParams = new ArrayList<>(Arrays.asList(recipeParamOne, recipeParamTwo, recipeParamThree));
        builder = Product.builder()
                .longName("newProduct")
                .shortName("newProduct")
                .type(ProductType.SELLABLE)
                .status(ProductStatus.ACTIVE)
                .rapidCode(110)
                .quantityUnit(QuantityUnit.CENTILITER)
                .storageMultiplier(Double.valueOf(100))
                .purchasePrice(440)
                .salePrice(220)
                .minimumStock(14)
                .stockWindow(60);

    }

    @Test
    public void testGetActiveProducts() {
        List<ProductAdapter> activeProducts = ProductAdapter.getActiveProducts();
        assertEquals(NUMBER_OF_PRODUCTS, activeProducts.size());
    }

    @Test
    public void testGetStorableProducts() {
        List<ProductAdapter> products = ProductAdapter.getStorableProducts();
        assertEquals(10, products.size());
        assertEquals(0, products.stream().filter(productAdapter ->
                productAdapter.getAdaptee().getLongName().equals("productFour")).collect(toList()).size());
    }

    @Test
    public void testGetAdHocProduct() {
        ProductAdapter adHocProduct = ProductAdapter.getAdHocProduct();
        assertEquals(ProductType.AD_HOC_PRODUCT, adHocProduct.getAdaptee().getType());
    }

    @Test
    public void testGetGameFeeProduct() {
        ProductAdapter gameFee = ProductAdapter.getGameFeeProduct();
        assertEquals(ProductType.GAME_FEE_PRODUCT, gameFee.getAdaptee().getType());
    }

    @Test
    public void testGetProductByName() {
        assertEquals("productOne", ProductAdapter.getProductByName("productOne").getAdaptee().getLongName());
    }

    @Test
    public void testGetProductByNameNull() {
        assertNull(ProductAdapter.getProductByName("noSuchProduct"));
    }
    @Test
    public void testUpdateRecipes() {
        recipeParams.get(0).setQuantity(1);
        productFour.updateRecipes(recipeParams);
        Product updatedProductFour = ProductAdapter.getProductByName("productFour").getAdaptee();
        assertEquals(1, updatedProductFour.getRecipes().stream()
                .filter(recipe -> recipe.getComponent().getLongName().equals("productRecipeElementOne"))
                .collect(toList()).get(0).getQuantityMultiplier(), 0.001);
        assertEquals(3, updatedProductFour.getRecipes().size());
    }

    @Test
    public void testAddRecipes() {
        recipeParams.add(RecipeParams.builder().componentName("productTwo").quantity(1).build());
        productFour.addRecipes(recipeParams);
        Product updatedProductFour = ProductAdapter.getProductByName("productFour").getAdaptee();
        assertEquals(1, updatedProductFour.getRecipes().stream()
                .filter(recipe -> recipe.getComponent().getLongName().equals("productTwo"))
                .collect(toList()).get(0).getQuantityMultiplier(), 0.001);
        assertEquals(4, updatedProductFour.getRecipes().size());
    }

    @Test
    public void testDeleteRecipes() {
        recipeParams.remove(recipeParamOne);
        productFour.deleteRecipes(recipeParams);
        Product updatedProductFour = ProductAdapter.getProductByName("productFour").getAdaptee();
        assertEquals(0, updatedProductFour.getRecipes().stream()
                .filter(recipe -> recipe.getComponent().getLongName().equals("productRecipeElementOne"))
                .collect(toList()).size(), 0.001);
        assertEquals(2, updatedProductFour.getRecipes().size());
    }

    @Test
    public void testDeleteProduct() {
        productFour.deleteProduct();
        Product productFourUpdated = (Product)GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME,
                query -> query.setParameter("longName", productFour.getAdaptee().getLongName())).get(0);
        assertEquals(ProductStatus.DELETED, productFourUpdated.getStatus());
        assertEquals(ProductStatus.DELETED, productFourUpdated.getCategory().getStatus());
    }

    @Test
    public void testUpdateProductCategoryNotChanged() {
        long productFourId = productFour.getAdaptee().getId();
        productFour.updateProduct(productFour.getAdaptee().getCategory().getParent().getName(), builder);
        ProductAdapter productFourUpdated = getProductById(productFourId);
        assertEquals("newProduct", productFourUpdated.getAdaptee().getLongName());
        assertEquals("newProduct", productFourUpdated.getAdaptee().getShortName());
        assertEquals(220, productFourUpdated.getAdaptee().getSalePrice());
    }

    @Test
    public void testUpdateProductNameNotChanged() {
        long productFourId = productFour.getAdaptee().getId();
        builder.longName(productFour.getAdaptee().getLongName());
        productFour.updateProduct(productFour.getAdaptee().getCategory().getParent().getName(), builder);
        ProductAdapter productFourUpdated = getProductById(productFourId);
        assertEquals(productFour.getAdaptee().getLongName(), productFourUpdated.getAdaptee().getLongName());
        assertEquals(220, productFourUpdated.getAdaptee().getSalePrice());
    }
    @Test
    public void testUpdateProductCategoryChanged() {
        ProductCategory originalParent = productFour.getAdaptee().getCategory().getParent();
        long productFourId = productFour.getAdaptee().getId();
        productFour.updateProduct("leafOne", builder);
        ProductAdapter productFourUpdated = getProductById(productFourId);
        assertEquals("leafOne", productFourUpdated.getAdaptee().getCategory().getParent().getName());
        assertEquals(0, originalParent.getChildren().stream()
                .filter(productCategory -> productCategory.getName().equals(productFourUpdated.getAdaptee().getCategory().getName()))
                .count());
    }

    @Test(expected = IllegalProductStateException.class)
    public void testUpdateProductNameAlreadyUsed() {
        builder.longName("productOne");
        productFour.updateProduct(productFour.getAdaptee().getCategory().getParent().getName(), builder);
    }
}
