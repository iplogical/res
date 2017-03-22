package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.waiter.application.Main.APP_HEIGHT;
import static com.inspirationlogical.receipt.waiter.application.Main.APP_WIDTH;
import static com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl.RESTAURANT_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;

/**
 * Created by Bálint on 2017.03.22..
 */
public class SaleViewControllerImpl implements SaleViewController {

    @FXML
    BorderPane root;

    @FXML
    AnchorPane center;

    @FXML
    VBox left;

    @FXML
    Button backToRestaurantView;

    private RetailServices retailServices;

    @Inject
    public SaleViewControllerImpl(RetailServices retailServices) {
        this.retailServices = retailServices;
    }

    public static final String SALE_VIEW_PATH = "/view/fxml/SaleView.fxml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        Parent root = (Parent) loadView(RESTAURANT_VIEW_PATH, getInjector().getInstance(RestaurantController.class));
        Main.getWindow().hide();
        Main.getWindow().setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        Main.getWindow().setFullScreen(true);
        Main.getWindow().show();
    }
}
