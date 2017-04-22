package com.inspirationlogical.receipt.waiter.registry;

import com.google.inject.Provides;
import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RestaurantServiceImpl;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.service.RetailServiceImpl;
import com.inspirationlogical.receipt.waiter.controller.*;

public class WaiterRegistry extends Registry {

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector(WaiterRegistry.class).getInstance(clazz);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(RestaurantController.class).to(RestaurantControllerImpl.class);
        bind(SaleController.class).to(SaleControllerImpl.class);
        bind(PaymentController.class).to(PaymentControllerImpl.class);
        bind(DailySummaryController.class).to(DailySummaryControllerImpl.class);
        bind(TableFormController.class).to(TableFormControllerImpl.class);
        bind(AdHocProductFormController.class).to(AdHocProductFormControllerImpl.class);
        bind(RestaurantService.class).to(RestaurantServiceImpl.class);
        bind(RetailService.class).to(RetailServiceImpl.class);
    }

    @Provides
    TableController provideTableController(RestaurantController restaurantController,
                                           RestaurantService restaurantService,
                                           RetailService retailService) {

        TableController tableController = new TableControllerImpl(restaurantController, restaurantService, retailService);

        injector.injectMembers(tableController);

        return tableController;
    }
}