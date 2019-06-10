package com.inspirationlogical.receipt.manager.controller.stock;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.StockView;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.utility.NotificationMessage;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsFxmlView;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewstate.StockViewState;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@FXMLController
public class StockControllerImpl extends AbstractController implements StockController {

    @FXML
    private BorderPane root;

    private @FXML
    TreeTableView<ProductCategoryView> categoriesTable;
    private @FXML
    TreeTableColumn<ProductCategoryView, String> categoryName;

    @FXML
    private TableView<StockView> stockTable;
    @FXML
    private TableColumn<StockView, String> productLongName;
    @FXML
    private TableColumn<StockView, String> stockAvailableQuantity;
    @FXML
    private TableColumn<StockView, String> stockInitialQuantity;
    @FXML
    private TableColumn<StockView, String> stockSoldQuantity;
    @FXML
    private TableColumn<StockView, String> stockPurchasedQuantity;
    @FXML
    private TableColumn<StockView, String> stockInventoryQuantity;
    @FXML
    private TableColumn<StockView, String> stockDisposedQuantity;
    @FXML
    private TableColumn<StockView, String> stockInputQuantity;
    @FXML
    private TableColumn<StockView, String> productQuantityUnit;
    @FXML
    private TableColumn<StockView, String> productStorageMultiplier;
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

    private StockViewState stockViewState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stockViewState = new StockViewState();
        initColumns();
        initActionTypeToggles();
        initCheckBox();
        initCategories();
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @FXML
    public void onShowGoods(Event event) {
        ManagerApp.showView(GoodsFxmlView.class);
    }

    @FXML
    public void onUpdateStock(Event event) {
        if (stockViewState.getReceiptType() == null) {
            NotificationMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("Stock.SelectReceiptType"));
            return;
        }
        try {
            List<StockParams> stockParamsList = stockTable.getItems().stream()
                    .filter(stockView -> stockView.getInputQuantity() != null)
                    .map(this::buildStockParams)
                    .collect(Collectors.toList());
            managerService.updateStock(stockParamsList, stockViewState.getReceiptType());
            hideInputColumn();
            actionTypeToggleGroup.selectToggle(null);
            refreshStockTable(getSelectedCategory());
        } catch (NumberFormatException e) {
            NotificationMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("Stock.NumberFormatQuantity"));
        }
    }

    private StockParams buildStockParams(StockView stockView) {
        return StockParams.builder()
                .productName(stockView.getProduct().getLongName())
                .quantity(stockView.getInputQuantity())
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
        ProductCategoryView selectedCategory = getSelectedCategory();
        if (selectedCategory != null) {
            refreshStockTable(selectedCategory);
        }
    }

    private ProductCategoryView getSelectedCategory() {
        return categoriesTable.getSelectionModel().getSelectedItem().getValue();
    }

    private void initColumns() {
        initColumn(categoryName, ProductCategoryView::getName);
        initColumn(productLongName, stockView -> stockView.getProduct().getLongName());
        initColumn(stockAvailableQuantity, stockView -> String.valueOf(stockView.getAvailableQuantity()));
        initColumn(stockInitialQuantity, stockView -> String.valueOf(stockView.getInitialQuantity()));
        initColumn(stockSoldQuantity, stockView -> String.valueOf(stockView.getSoldQuantity()));
        initColumn(stockPurchasedQuantity, stockView -> String.valueOf(stockView.getPurchasedQuantity()));
        initColumn(stockInventoryQuantity, stockView -> String.valueOf(stockView.getInventoryQuantity()));
        initColumn(stockDisposedQuantity, stockView -> String.valueOf(stockView.getDisposedQuantity()));
        initColumn(productQuantityUnit, stockView -> stockView.getProduct().getQuantityUnit().toI18nString());
        initColumn(productStorageMultiplier, stockView -> String.valueOf(stockView.getProduct().getStorageMultiplier()));
        initInputColumn(stockInputQuantity, (stockView, input) -> stockView.setInputQuantity(Double.valueOf(input)));
        hideInputColumn();
        stockTable.setEditable(true);
    }

    private void initCategories() {
        ProductCategoryView rootCategory = commonService.getRootProductCategory();
        TreeItem<ProductCategoryView> rootItem = new TreeItem<>(rootCategory);
        categoriesTable.setRoot(rootItem);
        categoriesTable.setShowRoot(false);
        updateCategory(rootCategory, rootItem);
        categoriesTable.getSelectionModel().selectedItemProperty().addListener(this::onCategoriesTableSelectionChanged);
    }

    private void onCategoriesTableSelectionChanged(ObservableValue<? extends TreeItem<ProductCategoryView>> obs,
                                                   TreeItem<ProductCategoryView> oldSelection,
                                                   TreeItem<ProductCategoryView> newSelection) {
        if (newSelection != null) {
            refreshStockTable(newSelection.getValue());
        }
    }

    private void updateCategory(ProductCategoryView productCategoryView, TreeItem<ProductCategoryView> parentTreeItem) {
        parentTreeItem.setExpanded(true);
        commonService.getChildCategories(productCategoryView).forEach(childCategory -> {
            if (childCategory.getStatus() == ProductStatus.ACTIVE) {
                TreeItem<ProductCategoryView> childTreeItem = addProductsAndRecipeItems(parentTreeItem, childCategory);
                updateCategory(childCategory, childTreeItem);
            }
        });
    }

    private TreeItem<ProductCategoryView> addProductsAndRecipeItems(TreeItem<ProductCategoryView> parentTreeItem, ProductCategoryView childCategory) {
        TreeItem<ProductCategoryView> childTreeItem = new TreeItem<>(childCategory);
        parentTreeItem.getChildren().add(childTreeItem);
        sortTreeItemChildren(parentTreeItem);
        return childTreeItem;
    }

    private void sortTreeItemChildren(TreeItem<ProductCategoryView> treeItem) {
        treeItem.getChildren().sort(Comparator.comparing(categoryViewModelTreeItem -> categoryViewModelTreeItem.getValue().getName()));
    }

    private void refreshStockTable(ProductCategoryView selectedCategory) {
        stockTable.getItems().clear();
        List<StockView> stockViewList = managerService.getStockViewListByCategory(selectedCategory);
        ObservableList<StockView> stockViewObservableList = FXCollections.observableArrayList(stockViewList);
        stockTable.setItems(stockViewObservableList);
        stockTable.refresh();
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
        initColumn(stockAvailableQuantity, stockView -> String.valueOf(stockView.getAvailableQuantity()));
        initColumn(stockInitialQuantity, stockView -> String.valueOf(stockView.getInitialQuantity()));
        initColumn(stockSoldQuantity, stockView -> String.valueOf(stockView.getSoldQuantity()));
        initColumn(stockPurchasedQuantity, stockView -> String.valueOf(stockView.getPurchasedQuantity()));
        initColumn(stockInventoryQuantity, stockView -> String.valueOf(stockView.getInventoryQuantity()));
        initColumn(stockDisposedQuantity, stockView -> String.valueOf(stockView.getDisposedQuantity()));
    }

    private void displayAbsoluteValues() {
        initColumn(stockAvailableQuantity, stockView -> String.valueOf(stockView.getAvailableQuantityAbsolute()));
        initColumn(stockInitialQuantity, stockView -> String.valueOf(stockView.getInitialQuantityAbsolute()));
        initColumn(stockSoldQuantity, stockView -> String.valueOf(stockView.getSoldQuantityAbsolute()));
        initColumn(stockPurchasedQuantity, stockView -> String.valueOf(stockView.getPurchasedQuantityAbsolute()));
        initColumn(stockInventoryQuantity, stockView -> String.valueOf(stockView.getInventoryQuantityAbsolute()));
        initColumn(stockDisposedQuantity, stockView -> String.valueOf(stockView.getDisposedQuantityAbsolute()));
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
