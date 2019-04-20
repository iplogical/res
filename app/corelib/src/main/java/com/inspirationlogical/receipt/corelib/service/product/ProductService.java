package com.inspirationlogical.receipt.corelib.service.product;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.RecipeView;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;

import java.util.List;

public interface ProductService {

    void addProduct(ProductCategoryView parent, Product.ProductBuilder builder);

    void updateProduct(int productId, String parentCategoryName, Product.ProductBuilder builder);

    void deleteProduct(String longName);

    List<RecipeView> getRecipeComponents(ProductView product);

    void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList);

    List<Product> getStorableProducts();

    List<ProductView> getProductsByCategory(ProductCategoryView productCategoryView, boolean showDeleted);
}
