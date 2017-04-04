package com.inspirationlogical.receipt.waiter.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Setter;

/**
 * Created by Bálint on 2017.03.28..
 */
public class AbstractRetailControllerImpl {

    @FXML
    protected Label tableName;
    @FXML
    protected Label tableNumber;
    @FXML
    protected Label totalPrice;
    @FXML
    protected Label note;

    @FXML
    protected javafx.scene.control.TableView<SoldProductViewModel> soldProductsTable;
    @FXML
    protected TableColumn productName;
    @FXML
    protected TableColumn productQuantity;
    @FXML
    protected TableColumn productUnitPrice;
    @FXML
    protected TableColumn productTotalPrice;

    protected RestaurantService restaurantService;

    protected RetailService retailService;

    protected RestaurantController restaurantController;

    protected @Setter TableView tableView;

    protected ReceiptView receiptView;

    protected Collection<ReceiptRecordView> soldProductsView;

    protected ObservableList<SoldProductViewModel> soldProductsModel;

    protected boolean soldProductsTableInitialized = false;

    public AbstractRetailControllerImpl(RestaurantService restaurantService,
                                        RetailService retailService,
                                        RestaurantController restaurantController) {
        this.restaurantService = restaurantService;
        this.retailService = retailService;
        this.restaurantController = restaurantController;
        soldProductsModel = FXCollections.observableArrayList();
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        restaurantController.updateTables();
        Parent root = (Parent) restaurantController.getRootNode();
        WaiterApp.getWindow().getScene().setRoot(root);
    }

    protected void updateTableSummary() {
        tableName.setText(tableView.getName());
        tableNumber.setText(String.valueOf(tableView.getTableNumber()) + getGuestPerCapacity());
        note.setText(tableView.getNote());
    }

    protected String getGuestPerCapacity() {
        return " (" + String.valueOf(tableView.getGuestCount()) + "/" + String.valueOf(tableView.getTableCapacity()) + ")";
    }

    protected void initializeSoldProductsTable() {
        soldProductsTable.setEditable(true);
        productName.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productName"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productQuantity"));
        productUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productUnitPrice"));
        productTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productTotalPrice"));
        soldProductsTableInitialized = true;
    }


    protected Collection<ReceiptRecordView> getSoldProducts(RestaurantService restaurantService, TableView tableView) {
        receiptView = restaurantService.getActiveReceipt(tableView);
        return receiptView.getSoldProducts();
    }

    protected ObservableList<SoldProductViewModel> convertReceiptRecordViewsToModel(Collection<ReceiptRecordView> soldProducts) {
        List<SoldProductViewModel> list = soldProducts.stream().map(SoldProductViewModel::new).collect(Collectors.toList());
        return FXCollections.observableArrayList(list);
    }

    protected void updateSoldProductsTable(List<SoldProductViewModel> soldProducts) {
        soldProductsModel = FXCollections.observableArrayList();
        soldProductsModel.addAll(soldProducts);
        soldProductsTable.setItems(soldProductsModel);
        updateSoldTotalPrice();
    }

    protected void updateSoldTotalPrice() {
        totalPrice.setText(SoldProductViewModel.getTotalPrice(soldProductsModel) + " Ft");
    }
}
