package com.inspirationlogical.receipt.manager.controller.stock;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewstate.StockViewState;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import com.inspirationlogical.receipt.manager.viewmodel.*;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@FXMLController
public class StockControllerImpl extends AbstractController implements StockController {

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

    @Autowired
    private GoodsController goodsController;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private StockListener.StockUpdateListener stockListener;

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
    public Node getRootNode() {
        return root;
    }

    @FXML
    public void onShowGoods(Event event) {
//        viewLoader.loadViewIntoScene(goodsController);
    }

    @FXML
    public void onUpdateStock(Event event) {
        if (stockViewState.getReceiptType() == null) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("Stock.SelectReceiptType"));
            return;
        }
        try {
            List<StockParams> stockParamsList = stockTable.getItems().stream()
                    .filter(stockViewModel -> stockViewModel.getInputQuantity() != null)
                    .map(this::buildStockParams)
                    .collect(Collectors.toList());
            managerService.updateStock(stockParamsList, stockViewState.getReceiptType(), stockListener);
            hideInputColumn();
            actionTypeToggleGroup.selectToggle(null);
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("Stock.NumberFormatQuantity"));
        }
    }

    private StockParams buildStockParams(StockViewModel stockViewModel) {
        return StockParams.builder()
                .productName(stockViewModel.getName())
                .quantity(Double.valueOf(stockViewModel.getInputQuantity()))
                .isAbsoluteQuantity(quantityDisplay.selectedProperty().getValue())
                .build();
    }

    @FXML
    public void onQuantityDisplayToggle(Event event) {
        if (stockViewState.getIsAbsoluteQuantity().getValue()) {
            displayAbsoluteValues();
        } else {
            displayUnitValues();
        }
        updateStockItems();
    }

    private void initColumns() {
        initColumn(productLongName, StockViewModel::getName);
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

    @Override
    public void updateStockItems() {
        stockTable.getItems().clear();
        managerService.getStockItems().forEach(stockView -> stockTable.getItems().add(new StockViewModel(stockView)));
        ObservableList<StockViewModel> items = stockTable.getItems();
        items.sort(Comparator.comparing(StockViewModel::getName));
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
            if (actionTypeToggleGroup.getSelectedToggle() == null) {
                hideInputColumn();
                stockViewState.setReceiptType(null);
                return;
            }
            stockViewState.setReceiptType((ReceiptType) actionTypeToggleGroup.getSelectedToggle().getUserData());
            showInputColumn();
        }
    }
}
