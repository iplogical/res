package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;

public interface RecipeFormController extends Controller {

    void fetchProducts();

    void loadRecipeForm(GoodsController goodsController);
}
