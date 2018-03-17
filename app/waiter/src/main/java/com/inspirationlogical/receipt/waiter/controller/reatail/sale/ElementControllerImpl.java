package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bálint on 2017.03.23..
 */
public class ElementControllerImpl<T extends AbstractView> implements ElementController<T> {

    public static final String SALE_VIEW_ELEMENT_PATH = "/view/fxml/SaleElement.fxml";

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

    @Autowired
    public ElementControllerImpl(SaleController saleController) {
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
    public String getViewPath() {
        return SALE_VIEW_ELEMENT_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void select() {}

    @Override
    public void onElementClicked(MouseEvent event) {}
}
