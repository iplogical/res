package com.inspirationlogical.receipt.manager.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryViewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
@Singleton
public class CategoryFormControllerImpl implements CategoryFormController {

    @FXML
    VBox root;
    @FXML
    TextField name;
    @FXML
    ChoiceBox<ProductCategoryType> type;
    @FXML
    ChoiceBox<ProductCategoryView> parent;

    public static String PRODUCT_CATEGORY_FORM_VIEW_PATH = "/view/fxml/CategoryForm.fxml";

    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    private ObservableList<ProductCategoryView> allCategories;

    private ObservableList<ProductCategoryView> parentCategories;

    private ProductCategoryView rootCategory;

    private ObservableList<ProductCategoryType> categoryTypes;

    private CategoryViewModel categoryViewModel;

    private String originalCategoryName;

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
        addDragAndDrop(root);
        rootCategory = commonService.getRootProductCategory();
        parentCategories = FXCollections.observableArrayList(commonService.getAggregateCategories());
        parentCategories.add(rootCategory);
        allCategories = FXCollections.observableArrayList(commonService.getLeafCategories());
        allCategories.add(rootCategory);
        allCategories.addAll(parentCategories);
        parent.setItems(parentCategories);
        parent.setConverter(new CategoryStringConverter(parentCategories));
        categoryTypes = FXCollections.observableArrayList(Arrays.asList(ProductCategoryType.AGGREGATE, ProductCategoryType.LEAF));
        type.setItems(categoryTypes);
    }

    @Override
    public void loadCategoryForm(GoodsController goodsController) {
        this.goodsController = goodsController;
        parentCategories = FXCollections.observableArrayList(commonService.getAggregateCategories());
        parentCategories.add(rootCategory);
        parent.setItems(parentCategories);
        parent.setDisable(false);
        type.setValue(null);
        type.setDisable(false);
        name.setText("");
        originalCategoryName = "";
    }

    @Override
    public void setCategory(CategoryViewModel categoryViewModel) {
        this.categoryViewModel = categoryViewModel;
        if(categoryViewModel != null) {
            CategoryStringConverter converterAll = new CategoryStringConverter(allCategories);
            CategoryStringConverter converterParent = new CategoryStringConverter(parentCategories);
            originalCategoryName = categoryViewModel.getName();
            name.setText(originalCategoryName);
            type.setValue(converterAll.fromString(categoryViewModel.getName()).getType());
            type.setDisable(true);
            String parentName = converterAll.fromString(categoryViewModel.getName()).getParent().getName();
            parent.setValue(converterParent.fromString(parentName));
            parent.setDisable(true);
        }
    }

    @FXML
    public void onConfirm(Event event) {
        if(type.getValue() == null) {
            ErrorMessage.showErrorMessage(root, Resources.MANAGER.getString("ProductCategoryTypeNull"));
            return;
        } else if(parent.getValue() == null) {
            ErrorMessage.showErrorMessage(root, Resources.MANAGER.getString("ProductCategoryParentNull"));
            return;
        } else if(name.getText().equals("")) {
            ErrorMessage.showErrorMessage(root, Resources.MANAGER.getString("ProductCategoryNameEmpty"));
            return;
        }
        goodsController.addCategory(
                ProductCategoryParams.builder()
                .parent(parent.getValue())
                .name(name.getText())
                .originalName(originalCategoryName)
                .type(type.getValue())
                .build());
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }
}
