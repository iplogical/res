package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.manager.viewmodel.GoodsTableViewModel;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
public interface CategoryFormController extends Controller {

    void loadCategoryForm(GoodsController goodsController);

    void setCategory(GoodsTableViewModel categoryViewModel);
}
