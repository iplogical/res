package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import lombok.Setter;

import java.util.Collection;

/**
 * Created by Bálint on 2017.03.28..
 */
public class AbstractRetailControllerImpl {

    protected RestaurantServices restaurantServices;

    protected RetailServices retailServices;

    protected RestaurantController restaurantController;

    protected @Setter TableView tableView;

    protected ReceiptView receiptView;

    protected Collection<ReceiptRecordView> soldProducts;

    public AbstractRetailControllerImpl(RestaurantServices restaurantServices,
                                        RetailServices retailServices,
                                        RestaurantController restaurantController) {
        this.restaurantServices = restaurantServices;
        this.retailServices = retailServices;
        this.restaurantController = restaurantController;
    }
}
