package com.inspirationlogical.receipt.waiter.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewState;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by Bálint on 2017.03.22..
 */
@Singleton
public class SaleViewControllerImpl implements SaleViewController {

    public static final String SALE_VIEW_PATH = "/view/fxml/SaleView.fxml";

    @FXML
    BorderPane root;

    @FXML
    AnchorPane center;

    @FXML
    VBox left;

    @FXML
    Button backToRestaurantView;

    private RestaurantController restaurantController;

    private RetailServices retailServices;

    private SaleViewState saleViewState;

    @Inject
    public SaleViewControllerImpl(RestaurantController restaurantController, RetailServices retailServices) {
        this.restaurantController = restaurantController;
        this.retailServices = retailServices;
        saleViewState = new SaleViewState();
        saleViewState.setFullScreen(restaurantController.getViewState().isFullScreen());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        Parent root = (Parent) restaurantController.getRootNode();
        Main.getWindow().getScene().setRoot(root);
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
