package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bálint on 2017.03.28..
 */
public class PaymentViewControllerImpl extends AbstractRetailControllerImpl
        implements PaymentViewController {

    public static final String PAYMENT_VIEW_PATH = "/view/fxml/PaymentView.fxml";

    @FXML
    private BorderPane root;

    private RetailServices retailServices;

    private RestaurantServices restaurantServices;

    private @Setter SaleViewController saleViewController;

    @Inject
    public PaymentViewControllerImpl(RetailServices retailServices,
                                     RestaurantServices restaurantServices,
                                     RestaurantController restaurantController) {
        super(restaurantServices, retailServices, restaurantController);
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        Parent root = (Parent) restaurantController.getRootNode();
        Main.getWindow().getScene().setRoot(root);
    }
}
