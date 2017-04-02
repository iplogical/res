package com.inspirationlogical.receipt.manager.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryViewModel;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;

public class GoodsControllerImpl implements GoodsController {

    public static final String GOODS_VIEW_PATH = "/view/fxml/Goods.fxml";

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
    TreeTableColumn<CategoryViewModel, String> productMinimumStore;

    @FXML
    TreeTableColumn<CategoryViewModel, String> productStoreWindow;

    private RestaurantServices restaurantServices;

    private ProductCategoryView rootCategory;

    @Inject
    public GoodsControllerImpl(RestaurantServices restaurantServices) {
        this.restaurantServices = restaurantServices;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initRoot();
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
        initColumn(productMinimumStore, CategoryViewModel::getMinimumStore);
        initColumn(productStoreWindow, CategoryViewModel::getStoreWindow);
    }

    private void initRoot() {
        rootCategory = restaurantServices.getRootProductCategory();
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
    public Node getRootNode() {
        return null;
    }
}
