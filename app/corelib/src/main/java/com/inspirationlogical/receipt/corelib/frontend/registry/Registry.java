package com.inspirationlogical.receipt.corelib.frontend.registry;

import com.google.inject.*;
import com.inspirationlogical.receipt.corelib.frontend.view.FXMLLoaderProvider;
import com.inspirationlogical.receipt.corelib.model.transaction.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.security.service.SecurityService;
import com.inspirationlogical.receipt.corelib.security.service.SecurityServiceImpl;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.CommonServiceImpl;
import com.inspirationlogical.receipt.corelib.service.EntityViews;
import com.inspirationlogical.receipt.corelib.service.EntityViewsImpl;
import lombok.SneakyThrows;

import javax.persistence.EntityManager;

public abstract class Registry extends AbstractModule {

    private static Injector injector;

    @Override
    protected void configure() {
//        bind(RestaurantService.class).to(RestaurantServiceImpl.class);
        bind(CommonService.class).to(CommonServiceImpl.class);
        bind(SecurityService.class).to(SecurityServiceImpl.class);
        bind(EntityViews.class).to(EntityViewsImpl.class);
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

//    @Provides
//    @Singleton
//    ViewLoader provideViewLoader(FXMLLoaderProvider fxmlLoaderProvider) {
//        return new ViewLoader(fxmlLoaderProvider);
//    }

    @Provides
    @Singleton
    EntityManager provideEntityManager() {
        return EntityManagerProvider.getEntityManager();
    }
}