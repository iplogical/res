package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Singleton;
import java.net.URL;
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
    ChoiceBox<ProductStatus> quantityUnit;
    @FXML
    TextField salePrice;
    @FXML
    TextField purchasePrice;
    @FXML
    TextField minimumStock;
    @FXML
    TextField stockWindow;

    private GoodsController goodsController;

    public static String PRODUCT_FORM_VIEW_PATH =  "/view/fxml/ProductForm.fxml";

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

    }

    @Override
    public void loadProductForm(GoodsController goodsController) {
        this.goodsController = goodsController;
    }
    @FXML
    public void onConfirm(Event event) {
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }

}
