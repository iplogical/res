package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
public interface ProductFormController extends Controller {

    void loadProductForm(GoodsController goodsController);

    void setProductViewModel(ProductView goodsTableViewModel);

    void setCategory(ProductCategoryView categoryViewModel);
}
