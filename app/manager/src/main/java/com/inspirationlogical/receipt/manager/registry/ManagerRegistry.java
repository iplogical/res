package com.inspirationlogical.receipt.manager.registry;

import com.google.inject.Provides;
import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.corelib.frontend.view.FXMLLoaderProvider;

public class ManagerRegistry extends Registry {

    @Override
    protected void configure() {

    }

    @Provides
    FXMLLoaderProvider provideLoaderProvider() {
        return new FXMLLoaderProvider(this);
    }
}