package com.inspirationlogical.receipt.manager.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.viewmodel.StockViewModel;
import com.inspirationlogical.receipt.manager.viewstate.StockViewState;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    TableColumn<StockViewModel, String> stockInitialQuantity;
    @FXML
    TableColumn<StockViewModel, String> stockSoldQuantity;
    @FXML
    TableColumn<StockViewModel, String> stockPurchasedQuantity;
    @FXML
    TableColumn<StockViewModel, String> stockInputQuantity;
    @FXML
    TableColumn<StockViewModel, String> stockDate;
    @FXML
    TableColumn<StockViewModel, String> productType;
    @FXML
    TableColumn<StockViewModel, String> productStatus;
    @FXML
    TableColumn<StockViewModel, String> productQuantityUnit;
    @FXML
    TableColumn<StockViewModel, String> productStorageMultiplier;
    @FXML
    CheckBox quantityDisplay;
    @FXML
    ToggleButton purchase;
    @FXML
    ToggleButton inventory;
    @FXML
    ToggleButton disposal;
    @FXML
    Button updateStock;
    @FXML
    ToggleGroup actionTypeToggleGroup;
    @FXML
    Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    private GoodsController goodsController;

    private CommonService commonService;

    private StockViewState stockViewState;

    @Inject
    public StockControllerImpl(GoodsController goodsController,
                               CommonService commonService) {
        this.goodsController = goodsController;
        this.commonService = commonService;
        this.stockViewState = new StockViewState();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initStockItems();
        initActionTypeToggles();
        initCheckBox();
    }

    @Override
    public String getViewPath() {
        return STOCK_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @FXML
    public void onShowGoods(Event event) {
        viewLoader.loadViewIntoScene(goodsController);
    }

    @FXML
    public void onUpdateStock(Event event) {
        if(stockViewState.getReceiptType() == null) return;
        List<StockParams> stockParamsList = stockTable.getItems().stream()
                .filter(stockViewModel -> stockViewModel.getInputQuantity() != null)
                .map(stockViewModel -> StockParams.builder()
                        .productName(stockViewModel.getLongName())
                        .quantity(Double.valueOf(stockViewModel.getInputQuantity()))
                        .isAbsoluteQuantity(quantityDisplay.selectedProperty().getValue())
                        .build())
                .collect(Collectors.toList());
        commonService.updateStock(stockParamsList, stockViewState.getReceiptType());
        hideInputColumn();
        actionTypeToggleGroup.selectToggle(null);
        updateStockItems();
    }

    @FXML
    public void onQuantityDisplayToggle(Event event) {
        if(stockViewState.getIsAbsoluteQuantity().getValue()) {
            displayAbsoluteValues();
            updateStockItems();
        } else {
            displayUnitValues();
            updateStockItems();
        }
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
        initColumn(stockInitialQuantity, StockViewModel::getInitialQuantity);
        initColumn(stockSoldQuantity, StockViewModel::getSoldQuantity);
        initColumn(stockPurchasedQuantity, StockViewModel::getPurchasedQuantity);
        initColumn(stockDate, StockViewModel::getDate);
        initColumn(productType, StockViewModel::getType);
        initColumn(productStatus, StockViewModel::getStatus);
        initColumn(productQuantityUnit, StockViewModel::getQuantityUnit);
        initColumn(productStorageMultiplier, StockViewModel::getStorageMultiplier);
        initInputColumn();
        stockTable.setEditable(true);
    }

    private void initStockItems() {
        updateStockItems();
    }

    private void updateStockItems() {
        stockTable.getItems().clear();
        commonService.getStockItems().forEach(stockView -> stockTable.getItems().add(new StockViewModel(stockView)));
        ObservableList<StockViewModel>  items = stockTable.getItems();
        items.sort(Comparator.comparing(StockViewModel::getLongName));
        stockTable.setItems(items);
    }

    private void initInputColumn() {
        stockInputQuantity.setCellFactory(TextFieldTableCell.forTableColumn());
        stockInputQuantity.setOnEditCommit(e -> {
            e.getRowValue().setInputQuantity(e.getNewValue());
        });
        hideInputColumn();
    }

    private void initActionTypeToggles() {
        purchase.setUserData(ReceiptType.PURCHASE);
        inventory.setUserData(ReceiptType.INVENTORY);
        disposal.setUserData(ReceiptType.DISPOSAL);
        actionTypeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(actionTypeToggleGroup.getSelectedToggle() == null) {
                    hideInputColumn();
                    stockViewState.setReceiptType(null);
                    return;
                }
                stockViewState.setReceiptType((ReceiptType)actionTypeToggleGroup.getSelectedToggle().getUserData());
                showInputColumn();
            }
        });
    }

    private void initCheckBox() {
        stockViewState.setIsAbsoluteQuantity(quantityDisplay.selectedProperty());
    }

    private void displayUnitValues() {
        initColumn(stockAvailableQuantity, StockViewModel::getAvailableQuantity);
        initColumn(stockInitialQuantity, StockViewModel::getInitialQuantity);
        initColumn(stockSoldQuantity, StockViewModel::getSoldQuantity);
        initColumn(stockPurchasedQuantity, StockViewModel::getPurchasedQuantity);
    }

    private void displayAbsoluteValues() {
        initColumn(stockAvailableQuantity, StockViewModel::getAvailableQuantityAbsolute);
        initColumn(stockInitialQuantity, StockViewModel::getInitialQuantityAbsolute);
        initColumn(stockSoldQuantity, StockViewModel::getSoldQuantityAbsolute);
        initColumn(stockPurchasedQuantity, StockViewModel::getPurchasedQuantityAbsolute);
    }

    private void showInputColumn() {
        stockInputQuantity.setVisible(true);
    }

    private void hideInputColumn() {
        stockInputQuantity.setVisible(false);
    }
}
