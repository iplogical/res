package com.inspirationlogical.receipt.waiter.registry;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
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
        bind(SaleController.class).to(SaleControllerImpl.class);
        bind(PaymentController.class).to(PaymentControllerImpl.class);
        bind(TableFormController.class).to(TableFormControllerImpl.class);
        bind(RestaurantServices.class).to(RestaurantServicesImpl.class);
        bind(RetailServices.class).to(RetailServicesImpl.class);
        bind(TableSettingsFormController.class).to(TableSettingsFormControllerImpl.class);
        bind(AdHocProductFormController.class).to(AdHocProductFormControllerImpl.class);
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


    //TODO: Try to get different implementation of the SaleElementController based on the type of the input param.
    // SaleElementControllerImpl::drawElement;
//        bind(SaleElementController.class).to(SaleElementControllerImpl.class);
//        bind(new TypeLiteral<SaleElementControllerImpl<ProductView>>() {}).to(SaleProductControllerImpl.class);
//        bind(new TypeLiteral<SaleElementControllerImpl<ProductCategoryView>>() {}).to(SaleCategoryControllerImpl.class);
//        bind(SaleElementControllerImpl.class).to(SaleProductControllerImpl.class);
//        bind(SaleElementControllerImpl.class).to(SaleCategoryControllerImpl.class);

//    @Provides
//    SaleElementControllerImpl<ProductView> provideSaleViewElementControllerOfProductView(SaleViewController saleViewController) {
//        return new SaleProductControllerImpl(saleViewController);
//    }
//
//    @Provides
//    SaleElementControllerImpl<ProductCategoryView> provideSaleViewElementControllerOfProductCategoryView(SaleViewController saleViewController) {
//        return new SaleCategoryControllerImpl(saleViewController);
//    }
}