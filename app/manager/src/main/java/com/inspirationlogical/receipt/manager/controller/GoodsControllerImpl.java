package com.inspirationlogical.receipt.manager.controller;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.net.URL;
import java.util.ResourceBundle;

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
    TreeTableView<CategoryViewModel> categories;

    @FXML
    TreeTableColumn<CategoryViewModel, String> categoryName;

    @FXML
    TreeTableColumn<CategoryViewModel, String> productLongName;

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

    private void initColumns() {
        categoryName.setCellValueFactory((CellDataFeatures<CategoryViewModel, String> category) ->
                new ReadOnlyStringWrapper(category.getValue().getValue().getName())
        );
        productLongName.setCellValueFactory((CellDataFeatures<CategoryViewModel, String> product) ->
                new ReadOnlyStringWrapper(product.getValue().getValue().hasProduct()
                        ? product.getValue().getValue().getProductViewModel().getLongName()
                        : EMPTY)
        );
    }

    private void initRoot() {
        rootCategory = restaurantServices.getRootProductCategory();
        categories.setRoot(new TreeItem<>(new CategoryViewModel(rootCategory)));
    }

    @Override
    public Node getRootNode() {
        return null;
    }
}
