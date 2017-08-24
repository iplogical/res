package com.inspirationlogical.receipt.manager.registry;

import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.service.ManagerServiceImpl;
import com.inspirationlogical.receipt.manager.controller.goods.*;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.PriceModifierController;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.PriceModifierControllerImpl;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.PriceModifierFormController;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.PriceModifierFormControllerImpl;
import com.inspirationlogical.receipt.manager.controller.receipt.ReceiptController;
import com.inspirationlogical.receipt.manager.controller.receipt.ReceiptControllerImpl;
import com.inspirationlogical.receipt.manager.controller.stock.StockController;
import com.inspirationlogical.receipt.manager.controller.stock.StockControllerImpl;

public class ManagerRegistry extends Registry {

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector(ManagerRegistry.class).getInstance(clazz);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(ManagerService.class).to(ManagerServiceImpl.class);
        bind(GoodsController.class).to(GoodsControllerImpl.class);
        bind(StockController.class).to(StockControllerImpl.class);
        bind(PriceModifierController.class).to(PriceModifierControllerImpl.class);
        bind(PriceModifierFormController.class).to(PriceModifierFormControllerImpl.class);
        bind(ReceiptController.class).to(ReceiptControllerImpl.class);
        bind(ProductFormController.class).to(ProductFormControllerImpl.class);
        bind(CategoryFormController.class).to(CategoryFormControllerImpl.class);
        bind(RecipeFormController.class).to(RecipeFormControllerImpl.class);
    }
}