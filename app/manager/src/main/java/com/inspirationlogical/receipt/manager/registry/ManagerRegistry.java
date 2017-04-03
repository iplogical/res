package com.inspirationlogical.receipt.manager.registry;

import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.manager.controller.GoodsController;
import com.inspirationlogical.receipt.manager.controller.GoodsControllerImpl;
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
    }
}