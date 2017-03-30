package com.inspirationlogical.receipt.corelib.frontend.registry;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;

public abstract class Registry extends AbstractModule {

    protected static Injector injector;

    @Override
    protected void configure() {

    }

    @Provides
    EntityManager provideEntityManager() {
        return EntityManagerProvider.getEntityManager();
    }
}