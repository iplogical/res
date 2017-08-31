package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.manager.viewmodel.GoodsTableViewModel;

public interface RecipeFormController extends Controller {

    void initProducts();

    void loadRecipeForm(GoodsController goodsController);

    void setSelectedProduct(GoodsTableViewModel selectedGoodsValue);

    void updateComponentsTable();
}
