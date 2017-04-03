package com.inspirationlogical.receipt.manager.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryViewModel;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

@Singleton
public class GoodsControllerImpl implements GoodsController {

    public static final String GOODS_VIEW_PATH = "/view/fxml/Goods.fxml";

    @FXML
    private BorderPane root;
    @FXML
    TreeTableView<CategoryViewModel> goodsTable;
    @FXML
    TreeTableColumn<CategoryViewModel, String> categoryName;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productLongName;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productShortName;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productRapidCode;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productType;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productStatus;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productQuantityUnit;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productQuantityMultiplier;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productPurchasePrice;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productSalePrice;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productVATLocal;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productVATTakeAway;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productMinimumStock;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productStockWindow;
    @FXML
    Button createCategory;
    @FXML
    Button modifyCategory;
    @FXML
    Button deleteCategory;
    @FXML
    Button createProduct;
    @FXML
    Button modifyProduct;
    @FXML
    Button deleteProduct;
    @FXML
    Button showStock;

    @Inject
    private ViewLoader viewLoader;

    private StockController stockController;

    private CommonService commonService;

    private ProductCategoryView rootCategory;

    @Inject
    public GoodsControllerImpl(StockController stockController, CommonService commonService) {
        this.stockController = stockController;
        this.commonService = commonService;
    }

    @FXML
    public void onShowStock(Event event) {
        viewLoader.loadViewIntoScene(ManagerApp.getWindow(), stockController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initCategories();
    }

    private CategoryViewModel getViewModel(CellDataFeatures<CategoryViewModel, String> cellDataFeatures) {
        return cellDataFeatures.getValue().getValue();
    }

    private void initColumn(TreeTableColumn<CategoryViewModel, String> treeTableColumn, Function<CategoryViewModel, String> method) {
        treeTableColumn.setCellValueFactory((CellDataFeatures<CategoryViewModel, String> category) ->
                new ReadOnlyStringWrapper(method.apply(getViewModel(category))));
    }

    private void initColumns() {
        initColumn(categoryName, CategoryViewModel::getName);
        initColumn(productLongName, CategoryViewModel::getLongName);
        initColumn(productShortName, CategoryViewModel::getShortName);
        initColumn(productRapidCode, CategoryViewModel::getRapidCode);
        initColumn(productType, CategoryViewModel::getType);
        initColumn(productStatus, CategoryViewModel::getStatus);
        initColumn(productQuantityUnit, CategoryViewModel::getQuantityUnit);
        initColumn(productQuantityMultiplier, CategoryViewModel::getQuantityMultiplier);
        initColumn(productPurchasePrice, CategoryViewModel::getPurchasePrice);
        initColumn(productSalePrice, CategoryViewModel::getSalePrice);
        initColumn(productVATLocal, CategoryViewModel::getVATLocal);
        initColumn(productVATTakeAway, CategoryViewModel::getVATTakeAway);
        initColumn(productMinimumStock, CategoryViewModel::getMinimumStock);
        initColumn(productStockWindow, CategoryViewModel::getStockWindow);
    }

    private void initCategories() {
        rootCategory = commonService.getRootProductCategory();
        TreeItem<CategoryViewModel> rootItem = new TreeItem<>(new CategoryViewModel(rootCategory));
        goodsTable.setRoot(rootItem);
        updateCategory(rootCategory, rootItem);
    }

    private void updateCategory(ProductCategoryView productCategoryView, TreeItem<CategoryViewModel> treeItem) {
        treeItem.setExpanded(true);
        productCategoryView.getChildrenCategories().forEach(child -> {
            TreeItem<CategoryViewModel> childItem = new TreeItem<>(new CategoryViewModel(child));
            treeItem.getChildren().add(childItem);
            updateCategory(child, childItem);
        });
    }

    @Override
    public String getViewPath() {
        return GOODS_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
