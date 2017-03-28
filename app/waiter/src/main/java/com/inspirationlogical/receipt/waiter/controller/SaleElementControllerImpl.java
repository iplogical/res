package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewElementState;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bálint on 2017.03.23..
 */
public class SaleElementControllerImpl<T extends AbstractView> implements SaleElementController<T> {

    public static final String SALE_VIEW_ELEMENT_PATH = "/view/fxml/SaleViewElement.fxml";

    @FXML
    Label root;

    @FXML
    VBox vBox;

    @FXML
    Label elementName;

    @FXML
    Label salePrice;

    protected SaleController saleController;

    protected @Setter @Getter T view;

    protected SaleViewElementState saleViewElementState;

    @Inject
    public SaleElementControllerImpl(SaleController saleController) {
        this.saleController = saleController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateNode();
    }

    private void updateNode() {
        elementName.setText(view.getName());
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void select(boolean isSelected) {

    }

    @Override
    public void onElementClicked(MouseEvent event) {
        System.out.println("Clicked Element");
    }
}
