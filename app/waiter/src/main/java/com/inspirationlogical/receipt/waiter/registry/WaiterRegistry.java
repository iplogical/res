package com.inspirationlogical.receipt.waiter.registry;

import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.service.RetailServiceImpl;
import com.inspirationlogical.receipt.waiter.controller.dailysummary.DailySummaryController;
import com.inspirationlogical.receipt.waiter.controller.dailysummary.DailySummaryControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.*;
import com.inspirationlogical.receipt.waiter.controller.reservation.ReservationController;
import com.inspirationlogical.receipt.waiter.controller.reservation.ReservationControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.table.*;

public class WaiterRegistry extends Registry {

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector(WaiterRegistry.class).getInstance(clazz);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(RestaurantController.class).to(RestaurantControllerImpl.class);
        bind(TableConfigurationController.class).to(TableConfigurationControllerImpl.class);
        bind(SaleController.class).to(SaleControllerImpl.class);
        bind(ProductsAndCategoriesController.class).to(ProductsAndCategoriesControllerImpl.class);
        bind(PaymentController.class).to(PaymentControllerImpl.class);
        bind(DailySummaryController.class).to(DailySummaryControllerImpl.class);
        bind(ReservationController.class).to(ReservationControllerImpl.class);
        bind(TableController.class).to(TableControllerImpl.class);
        bind(TableFormController.class).to(TableFormControllerImpl.class);
        bind(AdHocProductFormController.class).to(AdHocProductFormControllerImpl.class);
        bind(RetailService.class).to(RetailServiceImpl.class);
    }
}