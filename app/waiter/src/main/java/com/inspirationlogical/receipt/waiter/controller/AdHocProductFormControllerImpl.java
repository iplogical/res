package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

/**
 * Created by Bálint on 2017.03.21..
 */
@Singleton
public class AdHocProductFormControllerImpl implements AdHocProductFormController {

    public static final String AD_HOC_PRODUCT_FORM_VIEW_PATH = "/view/fxml/AdHocProductForm.fxml";

    @FXML
    private VBox root;

    @FXML
    private TextField productName;

    @FXML
    private TextField productQuantity;

    @FXML
    private TextField productSalePrice;

    @FXML
    private TextField productPurchasePrice;

    private SaleController saleController;

    AdHocProductParams adHocProductParams;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root);
        productQuantity.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        productSalePrice.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @Override
    public void loadAdHocProductForm(SaleController saleController) {
        this.saleController = saleController;
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        adHocProductParams = AdHocProductParams.builder()
                .name(productName.getText())
                .quantity(Integer.valueOf(productQuantity.getText()))
                .purchasePrice(Integer.valueOf(productPurchasePrice.getText()))
                .salePrice(Integer.valueOf(productSalePrice.getText()))
                .build();
        saleController.sellAdHocProduct(adHocProductParams);
    }

    @FXML
    public void onCancel(MouseEvent event) {
        hideNode(root);
    }

    @Override
    public String getViewPath() {
        return AD_HOC_PRODUCT_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

}

