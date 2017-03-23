package com.inspirationlogical.receipt.waiter.controller;


import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewState;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.waiter.application.Main.APP_HEIGHT;
import static com.inspirationlogical.receipt.waiter.application.Main.APP_WIDTH;
import static com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl.RESTAURANT_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;

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
    Label totalPrice;

    @FXML
    javafx.scene.control.TableView<SoldProductsTableModel> soldProductsTable;

    @FXML
    TableColumn productName;

    @FXML
    TableColumn productQuantity;

    @FXML
    TableColumn productUnitPrice;

    @FXML
    TableColumn productTotalPrice;

    @FXML
    Button backToRestaurantView;

    private RestaurantController restaurantController;


    private SaleViewState saleViewState;

    private RestaurantServices restaurantServices;

    private RetailServices retailServices;

    private TableView tableView;

    private ReceiptView receiptView;

    private Collection<ReceiptRecordView> soldProducts;

    public SaleViewControllerImpl(RetailServices retailServices,
                                  RestaurantServices restaurantServices,
                                  RestaurantController restaurantController,
                                  TableView tableView) {
        this.retailServices = retailServices;
        this.restaurantServices = restaurantServices;
        this.tableView = tableView;
        receiptView = restaurantServices.getActiveReceipt(tableView);
        soldProducts = receiptView.getSoldProducts();
        saleViewState = new SaleViewState();
        saleViewState.setFullScreen(restaurantController.getViewState().isFullScreen());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateNode();
    }

    private void updateNode() {
        updateSoldProductsTable();
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

    private void updateSoldProductsTable() {
        soldProductsTable.setEditable(true);
        productName.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productName"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productQuantity"));
        productUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productUnitPrice"));
        productTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productTotalPrice"));
        ObservableList<SoldProductsTableModel> soldProductList = FXCollections.observableArrayList();
        soldProductList.addAll(soldProducts.stream()
                .map(receiptRecordView -> new SoldProductsTableModel(receiptRecordView.getName(),
                        String.valueOf(receiptRecordView.getSoldQuantity()),
                        String.valueOf(receiptRecordView.getSalePrice()),
                        String.valueOf(receiptRecordView.getTotalPrice())))
                .collect(Collectors.toList()));
        soldProductsTable.setItems(soldProductList);
    }
}
