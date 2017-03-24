package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewElementState;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bálint on 2017.03.23..
 */
public class SaleViewElementControllerImpl<T extends AbstractView> implements SaleViewElementController<T> {

    public static final String SALE_VIEW_ELEMENT_PATH = "/view/fxml/SaleViewElement.fxml";

    @FXML
    Label root;

    @FXML
    Label elementName;

    @FXML
    Label salePrice;

    private SaleViewController saleViewController;

    private @Setter T view;

    private SaleViewElementState saleViewElementState;

    @Inject
    public SaleViewElementControllerImpl(SaleViewController saleViewController) {
        this.saleViewController = saleViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateNode();
    }

    private void updateNode() {
        elementName.setText(view.getName());
    }

    @FXML
    public void onElementClicked(MouseEvent event) {

    }

        @Override
    public Node getRootNode() {
        return root;
    }
}
