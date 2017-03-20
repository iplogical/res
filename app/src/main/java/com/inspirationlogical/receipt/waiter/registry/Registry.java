package com.inspirationlogical.receipt.waiter.registry;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.inspirationlogical.receipt.waiter.controller.TableFormController;
import com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl;
import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RestaurantServicesImpl;

public class Registry extends AbstractModule {

    @Override
    protected void configure() {
        bind(RestaurantController.class).to(RestaurantControllerImpl.class);
        bind(TableFormController.class).to(TableFormControllerImpl.class);
        bind(RestaurantServices.class).to(RestaurantServicesImpl.class);
    }

    @Provides
    EntityManager provideEntityManager() {
        return EntityManagerProvider.getEntityManager();
    }
}