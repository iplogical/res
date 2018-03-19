package com.inspirationlogical.receipt.waiter.controller.reatail.sale.buttons;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
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

@FXMLController
@Scope("prototype")
public class CategoryControllerImpl implements CategoryController {

    @FXML
    private Label categoryRoot;

    @FXML
    private VBox vBox;

    @FXML
    private Label categoryName;

    @Autowired
    private SaleController saleController;

    @Autowired
    ProductsAndCategoriesController productsAndCategoriesController;

    @Getter
    @Setter
    private ProductCategoryView view;

    private boolean isSelected;

    @PostConstruct
    private void init() {
        view = productsAndCategoriesController.getProductCategoryViewBeingDrawn();
        productsAndCategoriesController.setCategoryControllerBeingDrawn(this);
    }

    @Override
    public Control getRoot() {
        return categoryRoot;
    }

    @Override
    public void select() {
        this.isSelected = true;
        CSSUtilities.setBorderColor(isSelected, vBox);
    }

    @FXML
    @Override
    public void onCategoryClicked(MouseEvent event) {
        if(isSelected) {
            return;
        } else {
            categoryRoot.requestFocus();
            saleController.clearSearch();
            productsAndCategoriesController.selectCategory(this.getView());
        }
    }

    @Override
    public void updateNode() {
        categoryName.setText(view.getName());
    }
}
