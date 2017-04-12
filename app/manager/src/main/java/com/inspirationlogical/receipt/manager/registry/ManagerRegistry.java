package com.inspirationlogical.receipt.manager.registry;

import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.manager.controller.CategoryFormController;
import com.inspirationlogical.receipt.manager.controller.CategoryFormControllerImpl;
import com.inspirationlogical.receipt.manager.controller.GoodsController;
import com.inspirationlogical.receipt.manager.controller.GoodsControllerImpl;
import com.inspirationlogical.receipt.manager.controller.PriceModifierController;
import com.inspirationlogical.receipt.manager.controller.PriceModifierControllerImpl;
import com.inspirationlogical.receipt.manager.controller.PriceModifierFormController;
import com.inspirationlogical.receipt.manager.controller.PriceModifierFormControllerImpl;
import com.inspirationlogical.receipt.manager.controller.ProductFormController;
import com.inspirationlogical.receipt.manager.controller.ProductFormControllerImpl;
import com.inspirationlogical.receipt.manager.controller.RecipeFormController;
import com.inspirationlogical.receipt.manager.controller.RecipeFormControllerImpl;
import com.inspirationlogical.receipt.manager.controller.StockController;
import com.inspirationlogical.receipt.manager.controller.StockControllerImpl;

public class ManagerRegistry extends Registry {

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector(ManagerRegistry.class).getInstance(clazz);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(GoodsController.class).to(GoodsControllerImpl.class);
        bind(StockController.class).to(StockControllerImpl.class);
        bind(PriceModifierController.class).to(PriceModifierControllerImpl.class);
        bind(PriceModifierFormController.class).to(PriceModifierFormControllerImpl.class);
        bind(ProductFormController.class).to(ProductFormControllerImpl.class);
        bind(CategoryFormController.class).to(CategoryFormControllerImpl.class);
        bind(RecipeFormController.class).to(RecipeFormControllerImpl.class);
    }
}