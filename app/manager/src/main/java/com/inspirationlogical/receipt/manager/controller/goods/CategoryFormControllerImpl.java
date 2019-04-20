package com.inspirationlogical.receipt.manager.controller.goods;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addFormDragAndDrop;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.manager.exception.InvalidInputFormException;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryStringConverter;

import com.inspirationlogical.receipt.manager.viewmodel.GoodsTableViewModel;
import com.inspirationlogical.receipt.manager.viewmodel.ProductStatusStringConverter;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
public class CategoryFormControllerImpl implements CategoryFormController {

    private @FXML VBox root;
    private @FXML TextField name;
    private @FXML ChoiceBox<ProductCategoryType> type;
    private @FXML ChoiceBox<ProductStatus> status;
    private @FXML ChoiceBox<ProductCategoryView> parent;
    private @FXML TextField orderNumber;

    private GoodsController goodsController;

    @Autowired
    private CommonService commonService;

    private ObservableList<ProductCategoryView> allCategories;

    private ObservableList<ProductCategoryView> parentCategories;

    private String originalCategoryName;

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addFormDragAndDrop(root);
        initCategories();
        initCategoryTypes();
        initCategoryStatuses();
    }

    private void initCategories() {
        initParentCategories();
        initAllCategories();
    }

    private void initParentCategories() {
        ProductCategoryView rootCategory = commonService.getRootProductCategory();
        parentCategories = FXCollections.observableArrayList(commonService.getAggregateCategories());
        parentCategories.sort(Comparator.comparing(ProductCategoryView::getCategoryName));
        parentCategories.add(rootCategory);
        parent.setItems(parentCategories);
        parent.setConverter(new CategoryStringConverter(parentCategories));
    }

    private void initAllCategories() {
        allCategories = FXCollections.observableArrayList(commonService.getLeafCategories());
        allCategories.addAll(parentCategories);
    }

    private void initCategoryTypes() {
        ObservableList<ProductCategoryType> categoryTypes = FXCollections.observableArrayList(Arrays.asList(ProductCategoryType.AGGREGATE, ProductCategoryType.LEAF));
        type.setItems(categoryTypes);
        type.setConverter(new ProductCategoryTypeStringConverter(categoryTypes));
    }

    private void initCategoryStatuses() {
        ObservableList<ProductStatus> productStatuses = FXCollections.observableArrayList(Arrays.asList(ProductStatus.values()));
        productStatuses.sort(Comparator.comparing(ProductStatus::toI18nString));
        status.setItems(productStatuses);
        status.setConverter(new ProductStatusStringConverter(productStatuses));
    }

    @Override
    public void loadCategoryForm(GoodsController goodsController) {
        initCategories();
        this.goodsController = goodsController;
        parent.setDisable(false);
        parent.setValue(null);
        type.setValue(null);
        type.setDisable(false);
        status.setValue(ProductStatus.ACTIVE);
        name.clear();
        orderNumber.setText("0");
        originalCategoryName = "";
    }

    @Override
    public void setCategory(GoodsTableViewModel goodsTableViewModel) {
        CategoryStringConverter converterAll = new CategoryStringConverter(allCategories);
        CategoryStringConverter converterParent = new CategoryStringConverter(parentCategories);
        originalCategoryName = goodsTableViewModel.getName();
        name.setText(originalCategoryName);
        orderNumber.setText(goodsTableViewModel.getOrderNumber());
        type.setValue(converterAll.fromString(goodsTableViewModel.getName()).getType());
        type.setDisable(true);
        status.setValue(status.getConverter().fromString(goodsTableViewModel.getStatus()));
        String parentName = converterAll.fromString(goodsTableViewModel.getName()).getParent().getCategoryName();
        parent.setValue(converterParent.fromString(parentName));
        parent.setDisable(true);
    }

    @FXML
    public void onConfirm(Event event) {
        try {
            ProductCategoryParams params = buildProductCategoryParams();
            goodsController.addCategory(params);
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(getRootNode(),
                    ManagerResources.MANAGER.getString("Form.NumberFormatException"));
        } catch (InvalidInputFormException e) {
            ErrorMessage.showErrorMessage(getRootNode(),
                    ManagerResources.MANAGER.getString("Form.EmptyNameOrChoiceBox"));
        }
    }

    private ProductCategoryParams buildProductCategoryParams() throws NumberFormatException, InvalidInputFormException {
        if(isChoiceBoxEmpty() || isNameEmpty())
            throw new InvalidInputFormException("");
        return ProductCategoryParams.builder()
            .parent(parent.getValue())
            .name(name.getText())
            .originalName(originalCategoryName)
            .orderNumber(Integer.valueOf(orderNumber.getText()))
            .type(type.getValue())
            .status(status.getValue())
            .build();
    }

    private boolean isNameEmpty() {
        return name.getText().isEmpty();
    }

    private boolean isChoiceBoxEmpty() {
        return parent.getValue() == null || type.getValue() == null || status.getValue() == null;
    }

    @FXML
    public void onCancel(Event event) {
        goodsController.hideCategoryForm();
    }

    private class ProductCategoryTypeStringConverter extends StringConverter<ProductCategoryType> {
        private ObservableList<ProductCategoryType> productCategoryTypes;

        ProductCategoryTypeStringConverter(ObservableList<ProductCategoryType> productCategoryTypes) {
            this.productCategoryTypes = productCategoryTypes;
        }

        @Override
        public String toString(ProductCategoryType productCategoryType) {
            return productCategoryType.toI18nString();
        }

        @Override
        public ProductCategoryType fromString(String string) {
            return productCategoryTypes.stream().filter(productCategoryType -> productCategoryType.toI18nString().equals(string))
                    .collect(toList()).get(0);
        }
    }
}
