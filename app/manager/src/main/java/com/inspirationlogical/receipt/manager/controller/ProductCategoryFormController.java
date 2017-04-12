package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryViewModel;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
public interface ProductCategoryFormController extends Controller {

    void loadProductCategoryForm(GoodsController goodsController);

    void setCategory(CategoryViewModel categoryViewModel);
}
