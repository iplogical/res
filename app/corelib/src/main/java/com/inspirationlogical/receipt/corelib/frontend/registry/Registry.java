package com.inspirationlogical.receipt.corelib.frontend.registry;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.FXMLLoaderProvider;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.utils.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.security.service.SecurityService;
import com.inspirationlogical.receipt.corelib.security.service.SecurityServiceImpl;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.CommonServiceImpl;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RestaurantServiceImpl;

import lombok.SneakyThrows;

public abstract class Registry extends AbstractModule {

    protected static Injector injector;

    @Override
    protected void configure() {
        bind(RestaurantService.class).to(RestaurantServiceImpl.class);
        bind(CommonService.class).to(CommonServiceImpl.class);
        bind(SecurityService.class).to(SecurityServiceImpl.class);
    }

    @SneakyThrows
    protected static Injector getInjector(Class<? extends Module> clazz) {
        if (injector == null) {
            injector = Guice.createInjector(clazz.newInstance());
        }
        return injector;
    }

    @Provides
    @Singleton
    FXMLLoaderProvider provideLoaderProvider() {
        return new FXMLLoaderProvider(this);
    }

    @Provides
    @Singleton
    ViewLoader provideViewLoader(FXMLLoaderProvider fxmlLoaderProvider) {
        return new ViewLoader(fxmlLoaderProvider);
    }

    @Provides
    @Singleton
    EntityManager provideEntityManager() {
        return EntityManagerProvider.getEntityManager();
    }
}