package com.inspirationlogical.receipt.manager.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.viewmodel.StockViewModel;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

@Singleton
public class StockControllerImpl implements StockController {

    public static final String STOCK_VIEW_PATH = "/view/fxml/Stock.fxml";

    @FXML
    private BorderPane root;
    @FXML
    TableView<StockViewModel> stockTable;
    @FXML
    TableColumn<StockViewModel, String> productLongName;
    @FXML
    TableColumn<StockViewModel, String> stockAvailableQuantity;
    @FXML
    TableColumn<StockViewModel, String> stockSoldQuantity;
    @FXML
    TableColumn<StockViewModel, String> stockInitialQuantity;
    @FXML
    TableColumn<StockViewModel, String> stockDate;
    @FXML
    TableColumn<StockViewModel, String> productType;
    @FXML
    TableColumn<StockViewModel, String> productStatus;
    @FXML
    TableColumn<StockViewModel, String> productQuantityUnit;
    @FXML
    TableColumn<StockViewModel, String> productQuantityMultiplier;
    @FXML
    Button createItem;
    @FXML
    Button modifyItem;
    @FXML
    Button deleteItem;
    @FXML
    Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    private GoodsController goodsController;

    private CommonService commonService;

    @Inject
    public StockControllerImpl(GoodsController goodsController, CommonService commonService) {
        this.goodsController = goodsController;
        this.commonService = commonService;
    }

    @FXML
    public void onShowGoods(Event event) {
        viewLoader.loadViewIntoScene(ManagerApp.getWindow(), goodsController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initStockItems();
    }

    private StockViewModel getViewModel(TableColumn.CellDataFeatures<StockViewModel, String> cellDataFeatures) {
        return cellDataFeatures.getValue();
    }

    private void initColumn(TableColumn<StockViewModel, String> tableColumn, Function<StockViewModel, String> method) {
        tableColumn.setCellValueFactory((TableColumn.CellDataFeatures<StockViewModel, String> category) ->
                new ReadOnlyStringWrapper(method.apply(getViewModel(category))));
    }

    private void initColumns() {
        initColumn(productLongName, StockViewModel::getLongName);
        initColumn(stockAvailableQuantity, StockViewModel::getAvailableQuantity);
        initColumn(stockSoldQuantity, StockViewModel::getSoldQuantity);
        initColumn(stockInitialQuantity, StockViewModel::getInitialQuantity);
        initColumn(stockDate, StockViewModel::getDate);
        initColumn(productType, StockViewModel::getType);
        initColumn(productStatus, StockViewModel::getStatus);
        initColumn(productQuantityUnit, StockViewModel::getQuantityUnit);
        initColumn(productQuantityMultiplier, StockViewModel::getQuantityMultiplier);
    }

    private void initStockItems() {
        updateStockItems();
    }

    private void updateStockItems() {
        commonService.getStockItems().forEach(stockView -> stockTable.getItems().add(new StockViewModel(stockView)));
    }

    @Override
    public String getViewPath() {
        return STOCK_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
