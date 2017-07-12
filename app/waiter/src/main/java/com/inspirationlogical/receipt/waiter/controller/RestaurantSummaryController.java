package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.Set;

/**
 * Created by TheDagi on 2017. 07. 12..
 */
public class RestaurantSummaryController {

    private final Set<TableController> tableControllers;
    private final RestaurantView restaurantView;

    public RestaurantSummaryController(final Set<TableController> tableControllers, final RestaurantView restaurantView) {
        this.tableControllers = tableControllers;
        this.restaurantView = restaurantView;
    }

    public Task<ObservableList<String>> getTask() {
        return new Task<ObservableList<String>>() {
            @Override protected ObservableList<String> call() throws InterruptedException {
                ObservableList<String> values = FXCollections.observableArrayList();
                values.add(getTotalTableNumber());
                values.add(getOpenTableNumber());
                values.add(getTotalGuests());
                values.add(getTotalCapacity());
                values.add(getOpenConsumption());
                values.add(getPaidConsumption());
                return values;
            }
        };
    }

    private String getTotalTableNumber() {
        return String.valueOf(tableControllers.stream()
                .filter(tableController -> tableController.getView().isNormal())
                .count());
    }

    private String getOpenTableNumber() {
        return String.valueOf(tableControllers.stream()
                .filter(tableController -> tableController.getView().isNormal())
                .filter(tableController -> tableController.getView().isOpen())
                .count());
    }

    private String getTotalGuests() {
        return String.valueOf(tableControllers.stream()
                .filter(tableController -> !tableController.getView().isEmployee())
                .mapToInt(tableController -> tableController.getView().getGuestCount()).sum());
    }

    private String getTotalCapacity() {
        return String.valueOf(tableControllers.stream()
                .filter(tableController -> tableController.getView().isNormal())
                .mapToInt(tableController -> tableController.getView().getCapacity()).sum());
    }

    private String getOpenConsumption() {
        return String.valueOf(tableControllers.stream()
                .filter(tableController -> tableController.getView().isOpen())
                .mapToInt(tableController -> tableController.getView().getTotalPrice()).sum());
    }

    private String getPaidConsumption() {
        return String.valueOf(restaurantView.getConsumptionOfTheDay(receipt -> !receipt.getPaymentMethod().equals(PaymentMethod.COUPON)));
    }
}
