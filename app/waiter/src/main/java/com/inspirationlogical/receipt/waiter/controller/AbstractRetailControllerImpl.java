package com.inspirationlogical.receipt.waiter.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    protected javafx.scene.control.TableView<SoldProductsTableModel> soldProductsTable;
    @FXML
    protected TableColumn productName;
    @FXML
    protected TableColumn productQuantity;
    @FXML
    protected TableColumn productUnitPrice;
    @FXML
    protected TableColumn productTotalPrice;

    protected RestaurantServices restaurantServices;

    protected RetailServices retailServices;

    protected RestaurantController restaurantController;

    protected @Setter TableView tableView;

    protected ReceiptView receiptView;

    protected Collection<ReceiptRecordView> soldProductsView;

    protected ObservableList<SoldProductsTableModel> soldProductsModel;

    public AbstractRetailControllerImpl(RestaurantServices restaurantServices,
                                        RetailServices retailServices,
                                        RestaurantController restaurantController) {
        this.restaurantServices = restaurantServices;
        this.retailServices = retailServices;
        this.restaurantController = restaurantController;
        soldProductsModel = FXCollections.observableArrayList();
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        restaurantController.updateTables();
        Parent root = (Parent) restaurantController.getRootNode();
        Main.getWindow().getScene().setRoot(root);
    }

    protected void initializeTableSummary() {
        tableName.setText(tableView.getName());
        tableNumber.setText(String.valueOf(tableView.getTableNumber()));
        note.setText(tableView.getNote());
    }

    protected void initializeSoldProductsTable() {
        soldProductsTable.setEditable(true);
        productName.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productName"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productQuantity"));
        productUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productUnitPrice"));
        productTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productTotalPrice"));
    }

    protected Collection<ReceiptRecordView> getSoldProducts(RestaurantServices restaurantServices, TableView tableView) {
        receiptView = restaurantServices.getActiveReceipt(tableView);
        return receiptView.getSoldProducts();
    }

    protected List<SoldProductsTableModel> convertReceiptRecordViewsToModel(Collection<ReceiptRecordView> soldProducts) {
        return soldProducts.stream()
                .map(receiptRecordView -> new SoldProductsTableModel(receiptRecordView.getName(),
                        String.valueOf(receiptRecordView.getSoldQuantity()),
                        String.valueOf(receiptRecordView.getSalePrice()),
                        String.valueOf(receiptRecordView.getTotalPrice()),
                        String.valueOf(receiptRecordView.getId()),
                        String.valueOf(receiptRecordView.getDiscountPercent()),
                        String.valueOf(receiptRecordView.getVat())))
                .collect(Collectors.toList());
    }

    protected void updateSoldProductsTable(List<SoldProductsTableModel> soldProducts) {
        soldProductsModel = FXCollections.observableArrayList();
        soldProductsModel.addAll(soldProducts);
        soldProductsTable.setItems(soldProductsModel);
        updateSoldTotalPrice();
    }

    protected void updateSoldTotalPrice() {
        totalPrice.setText(SoldProductsTableModel.getTotalPrice(soldProductsModel) + " Ft");
    }
}
