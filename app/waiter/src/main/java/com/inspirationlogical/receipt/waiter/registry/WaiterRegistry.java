package com.inspirationlogical.receipt.waiter.registry;

import com.google.inject.Provides;
import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.service.RetailServiceImpl;
import com.inspirationlogical.receipt.waiter.controller.AdHocProductFormController;
import com.inspirationlogical.receipt.waiter.controller.AdHocProductFormControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.DailySummaryController;
import com.inspirationlogical.receipt.waiter.controller.DailySummaryControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.PaymentController;
import com.inspirationlogical.receipt.waiter.controller.PaymentControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.ReservationController;
import com.inspirationlogical.receipt.waiter.controller.ReservationControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.SaleController;
import com.inspirationlogical.receipt.waiter.controller.SaleControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.TableController;
import com.inspirationlogical.receipt.waiter.controller.TableControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.TableFormController;
import com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl;

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
        bind(ReservationController.class).to(ReservationControllerImpl.class);
        bind(TableFormController.class).to(TableFormControllerImpl.class);
        bind(AdHocProductFormController.class).to(AdHocProductFormControllerImpl.class);
        bind(RetailService.class).to(RetailServiceImpl.class);
    }

    @Provides
    TableController provideTableController(RestaurantController restaurantController, RetailService retailService) {
        TableController tableController = new TableControllerImpl(restaurantController, retailService);
        injector.injectMembers(tableController);
        return tableController;
    }
}