package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Product.ProductBuilder;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier.PriceModifierBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

public interface CommonService {
    ProductBuilder productBuilder();

    ProductCategoryView getRootProductCategory();

    List<ProductCategoryView> getAllCategories();

    List<ProductCategoryView> getAggregateCategories();

    List<ProductCategoryView> getLeafCategories();

    List<ProductCategoryView> getChildCategories(ProductCategoryView productCategoryView);

    void getChildCategoriesRecursively(ProductCategoryView current, List<ProductCategoryView> traversal);

    List<ProductCategoryView> getChildPseudoCategories(ProductCategoryView productCategoryView);

    List<ProductView> getActiveProducts();

    List<ProductView> getSellableProducts();

    List<ProductView> getSellableProducts(ProductCategoryView productCategoryView);

    List<ProductView> getStorableProducts();
}
