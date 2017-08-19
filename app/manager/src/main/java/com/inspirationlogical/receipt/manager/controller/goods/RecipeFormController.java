package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;

public interface RecipeFormController extends Controller {

    void fetchProducts();

    void loadRecipeForm(GoodsController goodsController);
}
