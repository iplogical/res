package com.inspirationlogical.receipt.registry;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.inspirationlogical.receipt.controller.ContextMenuController;
import com.inspirationlogical.receipt.controller.ContextMenuControllerImpl;
import com.inspirationlogical.receipt.controller.RestaurantController;
import com.inspirationlogical.receipt.controller.RestaurantControllerImpl;
import com.inspirationlogical.receipt.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.service.RestaurantServices;
import com.inspirationlogical.receipt.service.RestaurantServicesImpl;

public class Registry extends AbstractModule {

    @Override
    protected void configure() {
        bind(RestaurantController.class).to(RestaurantControllerImpl.class);
        bind(ContextMenuController.class).to(ContextMenuControllerImpl.class);
        bind(RestaurantServices.class).to(RestaurantServicesImpl.class);
    }

    @Provides
    EntityManager provideEntityManager() {
        return EntityManagerProvider.getEntityManager();
    }
}