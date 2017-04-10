package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
@Singleton
public class ProductFormControllerImpl implements ProductFormController {

    @FXML
    VBox root;
    @FXML
    TextField longName;
    @FXML
    TextField shortName;
    @FXML
    ChoiceBox<ProductType> type;
    @FXML
    ChoiceBox<ProductCategoryView> category;
    @FXML
    ChoiceBox<ProductStatus> status;
    @FXML
    TextField rapidCode;
    @FXML
    ChoiceBox<QuantityUnit> quantityUnit;
    @FXML
    TextField storageMultiplier;
    @FXML
    TextField salePrice;
    @FXML
    TextField purchasePrice;
    @FXML
    TextField minimumStock;
    @FXML
    TextField stockWindow;

    public static String PRODUCT_FORM_VIEW_PATH =  "/view/fxml/ProductForm.fxml";

    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    private ObservableList<ProductCategoryView> leafCategories;

    private ObservableList<ProductType> productTypes;

    private ObservableList<ProductStatus> productStatuses;

    private ObservableList<QuantityUnit> quantityUnits;

    @Override
    public String getViewPath() {
        return PRODUCT_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        leafCategories = FXCollections.observableArrayList(commonService.getLeafCategories());
        category.setItems(leafCategories);
        category.setConverter(new CategoryStringConverter(leafCategories));
        productTypes = FXCollections.observableArrayList(Arrays.asList(ProductType.SELLABLE, ProductType.PARTIALLY_PAYABLE, ProductType.STORABLE));
        type.setItems(productTypes);
        productStatuses = FXCollections.observableArrayList(Arrays.asList(ProductStatus.values()));
        status.setItems(productStatuses);
        quantityUnits = FXCollections.observableArrayList(Arrays.asList(QuantityUnit.values()));
        quantityUnit.setItems(quantityUnits);
    }

    @Override
    public void loadProductForm(GoodsController goodsController) {
        this.goodsController = goodsController;
    }

    @FXML
    public void onConfirm(Event event) {
        goodsController.addProduct(category.getValue(), commonService.productBuilder()
        .longName(longName.getText())
        .shortName(shortName.getText())
        .type(type.getValue())
        .status(status.getValue())
        .rapidCode(Integer.valueOf(rapidCode.getText()))
        .quantityUnit(quantityUnit.getValue())
        .storageMultiplier(Double.valueOf(storageMultiplier.getText()))
        .purchasePrice(Integer.valueOf(purchasePrice.getText()))
        .salePrice(Integer.valueOf(salePrice.getText()))
        .minimumStock(Integer.valueOf(minimumStock.getText()))
        .stockWindow(Integer.valueOf(stockWindow.getText())));
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }

}
