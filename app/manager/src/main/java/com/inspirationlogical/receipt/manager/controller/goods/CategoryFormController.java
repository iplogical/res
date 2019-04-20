package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
public interface CategoryFormController extends Controller {

    void loadCategoryForm(GoodsController goodsController);

    void setCategory(ProductCategoryView categoryViewModel);
}
