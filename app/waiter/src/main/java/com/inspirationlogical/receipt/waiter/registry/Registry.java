package com.inspirationlogical.receipt.waiter.registry;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.corelib.service.RetailServicesImpl;
import com.inspirationlogical.receipt.waiter.controller.*;
import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RestaurantServicesImpl;

public class Registry extends AbstractModule {

    @Override
    protected void configure() {
        bind(RestaurantController.class).to(RestaurantControllerImpl.class);
        bind(SaleViewController.class).to(SaleViewControllerImpl.class);
        bind(PaymentViewController.class).to(PaymentViewControllerImpl.class);
        bind(TableFormController.class).to(TableFormControllerImpl.class);
        bind(RestaurantServices.class).to(RestaurantServicesImpl.class);
        bind(RetailServices.class).to(RetailServicesImpl.class);
        bind(TableSettingsFormController.class).to(TableSettingsFormControllerImpl.class);
    }

    @Provides
    EntityManager provideEntityManager() {
        return EntityManagerProvider.getEntityManager();
    }

    @Provides
    TableController provideTableController(RestaurantController restaurantController,
                                           TableSettingsFormController tableSettingsFormController,
                                           RestaurantServices restaurantServices,
                                           RetailServices retailServices) {
        return new TableControllerImpl(restaurantController, tableSettingsFormController, restaurantServices, retailServices);
    }


    //TODO: Try to get different implementation of the SaleViewElementController based on the type of the input param.
    // SaleViewElementControllerImpl::drawElement;
//        bind(SaleViewElementController.class).to(SaleViewElementControllerImpl.class);
//        bind(new TypeLiteral<SaleViewElementControllerImpl<ProductView>>() {}).to(SaleViewProductControllerImpl.class);
//        bind(new TypeLiteral<SaleViewElementControllerImpl<ProductCategoryView>>() {}).to(SaleViewCategoryControllerImpl.class);
//        bind(SaleViewElementControllerImpl.class).to(SaleViewProductControllerImpl.class);
//        bind(SaleViewElementControllerImpl.class).to(SaleViewCategoryControllerImpl.class);

//    @Provides
//    SaleViewElementControllerImpl<ProductView> provideSaleViewElementControllerOfProductView(SaleViewController saleViewController) {
//        return new SaleViewProductControllerImpl(saleViewController);
//    }
//
//    @Provides
//    SaleViewElementControllerImpl<ProductCategoryView> provideSaleViewElementControllerOfProductCategoryView(SaleViewController saleViewController) {
//        return new SaleViewCategoryControllerImpl(saleViewController);
//    }
}