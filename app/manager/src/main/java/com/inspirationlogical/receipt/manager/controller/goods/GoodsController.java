package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;

public interface GoodsController extends Controller {

    void addProduct(ProductView productView, ProductCategoryView parent);

    void addCategory(ProductCategoryParams params);

    void updateGoods();

    void hideCategoryForm();

    void hideProductForm();

    void hideRecipeForm();
}
