package com.inspirationlogical.receipt.manager.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.viewmodel.ProductStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.RecipeViewModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
        addDragAndDrop(root);
        sellableProducts = FXCollections.observableArrayList(commonService.getSellableProducts());
        storalbeProducts = FXCollections.observableArrayList(commonService.getStorableProducts());
        owner.setItems(sellableProducts);
        owner.getSelectionModel().selectedItemProperty().addListener(new OwnerChoiceBoxListener());
        owner.setConverter(new ProductStringConverter(sellableProducts));
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
    public void fetchProducts() {
        sellableProducts.clear();
        sellableProducts.addAll(commonService.getSellableProducts());
        storalbeProducts.clear();
        storalbeProducts.addAll(commonService.getStorableProducts());
    }

    @Override
    public void loadRecipeForm(GoodsController goodsController) {
        this.goodsController = goodsController;
    }

    @FXML
    public void onConfirm(Event event) {
        if (owner.getSelectionModel().isEmpty()) return;
        List<RecipeParams> recipeParamsList = componentTable.getItems().stream()
                .map(recipeViewModel -> RecipeParams.builder()
                        .componentName(recipeViewModel.getComponent())
                        .quantity(Double.valueOf(recipeViewModel.getQuantity()))
                        .build())
                .collect(toList());
        commonService.updateRecipe(owner.getSelectionModel().getSelectedItem(), recipeParamsList);
    }

    @FXML
    public void onHide(Event event) {
        goodsController.updateGoods();
        componentTable.getItems().clear();
        hideNode(root);
    }

    @FXML
    public void onAdd(Event event) {
        if(owner.getSelectionModel().getSelectedItem() == null) return;
        if(component.getSelectionModel().getSelectedItem() == null) return;
        if(quantity.getText().equals("")) return;
        RecipeViewModel newComponent = RecipeViewModel.builder()
                .component(component.getSelectionModel().getSelectedItem().getLongName())
                .unit(component.getSelectionModel().getSelectedItem().getQuantityUnit().toString())
                .quantity(quantity.getText())
                .build();
        componentTable.getItems().add(newComponent);
    }

    @FXML
    public void onDelete(Event event) {
        if(componentTable.getSelectionModel().getSelectedItem() == null) return;
        componentTable.getItems().remove(componentTable.getSelectionModel().getSelectedItem());
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
