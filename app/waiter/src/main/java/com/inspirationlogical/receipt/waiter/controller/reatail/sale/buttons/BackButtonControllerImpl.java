package com.inspirationlogical.receipt.waiter.controller.reatail.sale.buttons;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
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

import static com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView.getBackButtonCategoryView;

@FXMLController
@Scope("prototype")
public class BackButtonControllerImpl implements CategoryController {

    @FXML
    private Label backButtonRoot;

    @FXML
    private VBox vBox;

    @FXML
    private Label backButtonName;

    @Getter
    @Setter
    private ProductCategoryView view;

    @Autowired
    private ProductsAndCategoriesController productsAndCategoriesController;

    @PostConstruct
    private void init() {
        view = getBackButtonCategoryView(WaiterResources.WAITER.getString("SaleView.BackButton"));
        productsAndCategoriesController.setCategoryControllerBeingDrawn(this);
    }

    @Override
    public Control getRoot() {
        return backButtonRoot;
    }

    @Override
    public void select() {

    }

    @FXML
    @Override
    public void onCategoryClicked(MouseEvent event) {
        productsAndCategoriesController.onBackButtonClicked();
    }

    @Override
    public void updateNode() {
        backButtonName.setText(WaiterResources.WAITER.getString("SaleView.BackButton"));
    }
}
