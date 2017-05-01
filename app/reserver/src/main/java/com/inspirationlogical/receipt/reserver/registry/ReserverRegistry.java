package com.inspirationlogical.receipt.reserver.registry;

import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;

public class ReserverRegistry extends Registry {

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector(ReserverRegistry.class).getInstance(clazz);
    }

    @Override
    protected void configure() {
        super.configure();
    }
}
