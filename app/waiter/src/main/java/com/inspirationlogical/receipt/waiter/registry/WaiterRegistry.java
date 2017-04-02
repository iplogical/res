package com.inspirationlogical.receipt.waiter.registry;

import com.google.inject.Provides;
import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.corelib.service.RetailServicesImpl;
import com.inspirationlogical.receipt.waiter.controller.AdHocProductFormController;
import com.inspirationlogical.receipt.waiter.controller.AdHocProductFormControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.PaymentController;
import com.inspirationlogical.receipt.waiter.controller.PaymentControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.SaleController;
import com.inspirationlogical.receipt.waiter.controller.SaleControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.TableController;
import com.inspirationlogical.receipt.waiter.controller.TableControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.TableFormController;
import com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.TableSettingsFormController;
import com.inspirationlogical.receipt.waiter.controller.TableSettingsFormControllerImpl;

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
        bind(TableFormController.class).to(TableFormControllerImpl.class);
        bind(RetailServices.class).to(RetailServicesImpl.class);
        bind(TableSettingsFormController.class).to(TableSettingsFormControllerImpl.class);
        bind(AdHocProductFormController.class).to(AdHocProductFormControllerImpl.class);
    }

    @Provides
    TableController provideTableController(RestaurantController restaurantController,
                                           TableSettingsFormController tableSettingsFormController,
                                           RestaurantServices restaurantServices,
                                           RetailServices retailServices) {

        TableController tableController = new TableControllerImpl(restaurantController, tableSettingsFormController,
                restaurantServices, retailServices);

        injector.injectMembers(tableController);

        return tableController;
    }
}