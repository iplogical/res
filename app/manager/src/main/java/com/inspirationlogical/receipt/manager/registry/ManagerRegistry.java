package com.inspirationlogical.receipt.manager.registry;

import com.google.inject.Guice;
import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.manager.controller.GoodsController;
import com.inspirationlogical.receipt.manager.controller.GoodsControllerImpl;

public class ManagerRegistry extends Registry {

    public static <T> T getInstance(Class<T> clazz) {
        if (injector == null) {
            injector = Guice.createInjector(new ManagerRegistry());
        }
        return injector.getInstance(clazz);
    }

    @Override
    protected void configure() {
        bind(GoodsController.class).to(GoodsControllerImpl.class);
    }
}