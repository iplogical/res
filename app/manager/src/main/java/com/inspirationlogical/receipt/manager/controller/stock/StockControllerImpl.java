package com.inspirationlogical.receipt.manager.controller.stock;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;
import com.inspirationlogical.receipt.manager.viewmodel.StockViewModel;
import com.inspirationlogical.receipt.manager.viewstate.StockViewState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Singleton
public class StockControllerImpl extends AbstractController implements StockController {

    public static final String STOCK_VIEW_PATH = "/view/fxml/Stock.fxml";

    @FXML
    private BorderPane root;
    @FXML
    private TableView<StockViewModel> stockTable;
    @FXML
    private TableColumn<StockViewModel, String> productLongName;
    @FXML
    private TableColumn<StockViewModel, String> stockAvailableQuantity;
    @FXML
    private TableColumn<StockViewModel, String> stockInitialQuantity;
    @FXML
    private TableColumn<StockViewModel, String> stockSoldQuantity;
    @FXML
    private TableColumn<StockViewModel, String> stockPurchasedQuantity;
    @FXML
    private TableColumn<StockViewModel, String> stockInventoryQuantity;
    @FXML
    private TableColumn<StockViewModel, String> stockDisposedQuantity;
    @FXML
    private TableColumn<StockViewModel, String> stockInputQuantity;
    @FXML
    private TableColumn<StockViewModel, String> stockDate;
    @FXML
    private TableColumn<StockViewModel, String> productType;
    @FXML
    private TableColumn<StockViewModel, String> productStatus;
    @FXML
    private TableColumn<StockViewModel, String> productQuantityUnit;
    @FXML
    private TableColumn<StockViewModel, String> productStorageMultiplier;
    @FXML
    private CheckBox quantityDisplay;
    @FXML
    private ToggleButton purchase;
    @FXML
    private ToggleButton inventory;
    @FXML
    private ToggleButton disposal;
    @FXML
    private Button updateStock;
    @FXML
    private ToggleGroup actionTypeToggleGroup;
    @FXML
    private Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    @Inject
    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    @Inject
    private ManagerService managerService;

    private StockViewState stockViewState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stockViewState = new StockViewState();
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
        managerService.updateStock(stockParamsList, stockViewState.getReceiptType());
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

    private void initColumns() {
        initColumn(productLongName, StockViewModel::getLongName);
        initColumn(stockAvailableQuantity, StockViewModel::getAvailableQuantity);
        initColumn(stockInitialQuantity, StockViewModel::getInitialQuantity);
        initColumn(stockSoldQuantity, StockViewModel::getSoldQuantity);
        initColumn(stockPurchasedQuantity, StockViewModel::getPurchasedQuantity);
        initColumn(stockInventoryQuantity, StockViewModel::getInventoryQuantity);
        initColumn(stockDisposedQuantity, StockViewModel::getDisposedQuantity);
        initColumn(stockDate, StockViewModel::getDate);
        initColumn(productType, StockViewModel::getType);
        initColumn(productStatus, StockViewModel::getStatus);
        initColumn(productQuantityUnit, StockViewModel::getQuantityUnit);
        initColumn(productStorageMultiplier, StockViewModel::getStorageMultiplier);
        initInputColumn(stockInputQuantity, StockViewModel::setInputQuantity);
        hideInputColumn();
        stockTable.setEditable(true);
    }

    private void initStockItems() {
        updateStockItems();
    }

    private void updateStockItems() {
        stockTable.getItems().clear();
        managerService.getStockItems().forEach(stockView -> stockTable.getItems().add(new StockViewModel(stockView)));
        ObservableList<StockViewModel>  items = stockTable.getItems();
        items.sort(Comparator.comparing(StockViewModel::getLongName));
        stockTable.setItems(items);
    }

    private void initActionTypeToggles() {
        purchase.setUserData(ReceiptType.PURCHASE);
        inventory.setUserData(ReceiptType.INVENTORY);
        disposal.setUserData(ReceiptType.DISPOSAL);
        actionTypeToggleGroup.selectedToggleProperty().addListener(new ActionTypeToggleListener());
    }

    private void initCheckBox() {
        stockViewState.setIsAbsoluteQuantity(quantityDisplay.selectedProperty());
    }

    private void displayUnitValues() {
        initColumn(stockAvailableQuantity, StockViewModel::getAvailableQuantity);
        initColumn(stockInitialQuantity, StockViewModel::getInitialQuantity);
        initColumn(stockSoldQuantity, StockViewModel::getSoldQuantity);
        initColumn(stockPurchasedQuantity, StockViewModel::getPurchasedQuantity);
        initColumn(stockInventoryQuantity, StockViewModel::getInventoryQuantity);
    }

    private void displayAbsoluteValues() {
        initColumn(stockAvailableQuantity, StockViewModel::getAvailableQuantityAbsolute);
        initColumn(stockInitialQuantity, StockViewModel::getInitialQuantityAbsolute);
        initColumn(stockSoldQuantity, StockViewModel::getSoldQuantityAbsolute);
        initColumn(stockPurchasedQuantity, StockViewModel::getPurchasedQuantityAbsolute);
        initColumn(stockInventoryQuantity, StockViewModel::getInventoryQuantityAbsolute);
    }

    private void showInputColumn() {
        stockInputQuantity.setVisible(true);
    }

    private void hideInputColumn() {
        stockInputQuantity.setVisible(false);
    }

    private class ActionTypeToggleListener implements ChangeListener<Toggle> {

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
    }
}
