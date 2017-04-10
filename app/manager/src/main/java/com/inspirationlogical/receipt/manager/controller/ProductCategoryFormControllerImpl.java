package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
public class ProductCategoryFormControllerImpl implements ProductCategoryFormController {

    @FXML
    VBox root;
    @FXML
    TextField name;
    @FXML
    ChoiceBox<ProductCategoryType> type;
    @FXML
    ChoiceBox<ProductCategoryView> parent;

    public static String PRODUCT_CATEGORY_FORM_VIEW_PATH =  "/view/fxml/ProductCategoryForm.fxml";

    private GoodsController goodsController;

    @Override
    public String getViewPath() {
        return PRODUCT_CATEGORY_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void loadProductCategoryForm(GoodsController goodsController) {
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
