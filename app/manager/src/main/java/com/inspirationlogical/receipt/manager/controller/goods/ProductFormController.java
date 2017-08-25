package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.manager.viewmodel.GoodsTableViewModel;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
public interface ProductFormController extends Controller {

    void loadProductForm(GoodsController goodsController);

    void setProductViewModel(GoodsTableViewModel goodsTableViewModel);

    void setCategory(GoodsTableViewModel categoryViewModel);
}
