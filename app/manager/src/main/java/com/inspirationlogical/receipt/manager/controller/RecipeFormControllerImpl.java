package com.inspirationlogical.receipt.manager.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Singleton;

import com.inspirationlogical.receipt.manager.viewmodel.RecipeViewModel;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
@Singleton
public class RecipeFormControllerImpl implements RecipeFormController {

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
    ChoiceBox<String> owner;
    @FXML
    ChoiceBox<String> component;
    @FXML
    TextField quantity;

    private GoodsController goodsController;

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

}
