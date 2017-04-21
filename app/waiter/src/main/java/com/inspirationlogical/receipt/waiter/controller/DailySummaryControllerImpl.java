package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by TheDagi on 2017. 04. 21..
 */
@Singleton
public class DailySummaryControllerImpl extends AbstractRetailControllerImpl
    implements DailySummaryController {

    public static final String DAILY_SUMMARY_VIEW_PATH = "/view/fxml/DailySummary.fxml";

    @FXML
    private BorderPane root;

    @Inject
    public DailySummaryControllerImpl(RestaurantService restaurantService, RetailService retailService, RestaurantController restaurantController) {
        super(restaurantService, retailService, restaurantController);
    }

    @Override
    public String getViewPath() {
        return DAILY_SUMMARY_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    protected void rowClickHandler(SoldProductViewModel row) {

    }

    @Override
    public void onBackToRestaurantView(Event event) {
        restaurantController.updateRestaurant();
        viewLoader.loadViewIntoScene(restaurantController);
    }
}
