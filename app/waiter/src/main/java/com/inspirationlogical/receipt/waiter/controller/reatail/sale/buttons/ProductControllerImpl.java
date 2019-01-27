package com.inspirationlogical.receipt.waiter.controller.reatail.sale.buttons;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;

/**
 * Created by Bálint on 2017.03.24..
 */
@FXMLController
@Scope("prototype")
public class ProductControllerImpl implements ProductController {

    @FXML
    private Label productRoot;

    @FXML
    private VBox vBox;

    @FXML
    private Label productName;

    @Autowired
    private SaleController saleController;

    @Autowired
    private ProductsAndCategoriesController productsAndCategoriesController;

    private ProductView view;

    @PostConstruct
    private void init() {
        view = productsAndCategoriesController.getProductViewBeingDrawn();
        productsAndCategoriesController.setProductControllerBeingDrawn(this);
    }

    @Override
    public Control getRoot() {
        return productRoot;
    }

    @Override
    public void select() {

    }

    @FXML
    @Override
    public void onProductClicked(MouseEvent event) {
        saleController.sellProduct(view);
    }

    @Override
    public void updateNode() {
        productName.setText(view.getName());
    }
}


