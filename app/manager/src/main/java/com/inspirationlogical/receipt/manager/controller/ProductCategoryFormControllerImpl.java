package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import javax.inject.Inject;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static java.util.stream.Collectors.toList;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
public class ProductCategoryFormControllerImpl implements ProductCategoryFormController {

    @FXML
    VBox root;
    @FXML
    TextField name;
    @FXML
    ChoiceBox<ProductCategoryType> type;
    @FXML
    ChoiceBox<ProductCategoryView> parent;

    public static String PRODUCT_CATEGORY_FORM_VIEW_PATH =  "/view/fxml/ProductCategoryForm.fxml";

    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    private ObservableList<ProductCategoryView> parentCategories;

    private ObservableList<ProductCategoryType> categoryTypes;

    @Override
    public String getViewPath() {
        return PRODUCT_CATEGORY_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parentCategories = FXCollections.observableArrayList(commonService.getAggregateCategories());
        categoryTypes = FXCollections.observableArrayList(Arrays.asList(ProductCategoryType.AGGREGATE, ProductCategoryType.LEAF));
        parent.setItems(parentCategories);
        parent.setConverter(new StringConverter<ProductCategoryView>() {
            @Override
            public String toString(ProductCategoryView object) {
                return object.getCategoryName();
            }

            @Override
            public ProductCategoryView fromString(String string) {
                return parentCategories.stream().filter(productCategoryView -> productCategoryView.getCategoryName().equals(string))
                        .collect(toList()).get(0);
            }
        });
        type.setItems(categoryTypes);
    }

    @Override
    public void loadProductCategoryForm(GoodsController goodsController) {
        this.goodsController = goodsController;
        parentCategories = FXCollections.observableArrayList(commonService.getAggregateCategories());
        parent.setItems(parentCategories);
    }

    @FXML
    public void onConfirm(Event event) {
        if(type.getValue() == null) {
            ErrorMessage.showErrorMessage(root, Resources.UI.getString("ProductCategoryTypeNull"));
            return;
        } else if(parent.getValue() == null) {
            ErrorMessage.showErrorMessage(root, Resources.UI.getString("ProductCategoryParentNull"));
            return;
        } else if(name.getText().equals("")) {
            ErrorMessage.showErrorMessage(root, Resources.UI.getString("ProductCategoryNameEmpty"));
            return;
        }
        goodsController.addProductCategory(parent.getValue(), name.getText(), type.getValue());
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }
}
