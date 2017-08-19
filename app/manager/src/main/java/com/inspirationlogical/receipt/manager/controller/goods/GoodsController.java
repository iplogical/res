package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;

public interface GoodsController extends Controller {

    void addProduct(Long productId, ProductCategoryView parent, Product.ProductBuilder builder);

    void addCategory(ProductCategoryParams params);

    void updateGoods();
}
