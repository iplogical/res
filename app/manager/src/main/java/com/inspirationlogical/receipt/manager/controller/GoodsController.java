package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;

public interface GoodsController extends Controller {

    void addProduct(ProductCategoryView parent, Product.ProductBuilder builder);

    void addProductCategory(ProductCategoryView parent, String name, ProductCategoryType type);
}
