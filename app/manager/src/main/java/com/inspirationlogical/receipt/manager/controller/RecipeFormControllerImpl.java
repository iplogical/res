package com.inspirationlogical.receipt.manager.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.RecipeView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.viewmodel.ProductStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.RecipeViewModel;

import com.inspirationlogical.receipt.manager.viewmodel.StockViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
@Singleton
public class RecipeFormControllerImpl extends AbstractController implements RecipeFormController {

    public static final String RECIPE_FORM_VIEW_PATH =  "/view/fxml/RecipeForm.fxml";

    @FXML
    VBox root;
    @FXML
    TableView<RecipeViewModel> componentTable;
    @FXML
    TableColumn<RecipeViewModel, String> componentName;
    @FXML
    TableColumn<RecipeViewModel, String> componentQuantity;
    @FXML
    TableColumn<RecipeViewModel, String> componentUnit;
    @FXML
    ChoiceBox<ProductView> owner;
    @FXML
    ChoiceBox<ProductView> component;
    @FXML
    TextField quantity;
    @FXML
    Label quantityUnit;

    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    private ObservableList<ProductView> sellableProducts;

    private ObservableList<ProductView> storalbeProducts;

    @Override
    public String getViewPath() {
        return RECIPE_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sellableProducts = FXCollections.observableArrayList(commonService.getSellableProducts());
        owner.setItems(sellableProducts);
        owner.getSelectionModel().selectedItemProperty().addListener(new OwnerChoiceBoxListener());
        owner.setConverter(new ProductStringConverter(sellableProducts));
        storalbeProducts = FXCollections.observableArrayList(commonService.getStorableProducts());
        component.setItems(storalbeProducts);
        component.setConverter(new ProductStringConverter(storalbeProducts));
        component.getSelectionModel().selectedItemProperty().addListener(new ComponentChoiceBoxListener());
        componentTable.setEditable(true);
        initColumn(componentName, RecipeViewModel::getComponent);
        initColumn(componentQuantity, RecipeViewModel::getQuantity);
        initInputColumn(componentQuantity, RecipeViewModel::setQuantity);
        initColumn(componentUnit, RecipeViewModel::getUnit);
    }

    @Override
    public void loadRecipeForm(GoodsController goodsController) {
        this.goodsController = goodsController;
    }

    @FXML
    public void onConfirm(Event event) {
        hideNode(root);
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }

    private class ComponentChoiceBoxListener implements ChangeListener<ProductView> {
        @Override
        public void changed(ObservableValue<? extends ProductView> observable, ProductView oldValue, ProductView newValue) {
            quantityUnit.setText(newValue.getQuantityUnit().toString());
        }
    }

    private class OwnerChoiceBoxListener implements ChangeListener<ProductView> {
        @Override
        public void changed(ObservableValue<? extends ProductView> observable, ProductView oldValue, ProductView newValue) {
            ObservableList<RecipeViewModel> items =
            FXCollections.observableArrayList(commonService.getRecipeComponents(newValue).stream().map(RecipeViewModel::new).collect(toList()));
            componentTable.setItems(items);
        }
    }
}
