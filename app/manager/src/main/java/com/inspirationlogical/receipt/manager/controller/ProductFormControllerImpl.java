package com.inspirationlogical.receipt.manager.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryStringConverter;

import com.inspirationlogical.receipt.manager.viewmodel.CategoryViewModel;
import com.inspirationlogical.receipt.manager.viewmodel.ProductViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
@Singleton
public class ProductFormControllerImpl implements ProductFormController {

    @FXML
    VBox root;
    @FXML
    TextField longName;
    @FXML
    TextField shortName;
    @FXML
    ChoiceBox<ProductType> type;
    @FXML
    ChoiceBox<ProductCategoryView> category;
    @FXML
    ChoiceBox<ProductStatus> status;
    @FXML
    TextField rapidCode;
    @FXML
    ChoiceBox<QuantityUnit> quantityUnit;
    @FXML
    TextField storageMultiplier;
    @FXML
    TextField salePrice;
    @FXML
    TextField purchasePrice;
    @FXML
    TextField minimumStock;
    @FXML
    TextField stockWindow;

    public static String PRODUCT_FORM_VIEW_PATH =  "/view/fxml/ProductForm.fxml";

    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    private ObservableList<ProductCategoryView> leafCategories;

    private ObservableList<ProductType> productTypes;

    private ObservableList<ProductStatus> productStatuses;

    private ObservableList<QuantityUnit> quantityUnits;

    private Long productId;

    @Override
    public String getViewPath() {
        return PRODUCT_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root);
        leafCategories = FXCollections.observableArrayList(commonService.getLeafCategories());
        category.setItems(leafCategories);
        category.setConverter(new CategoryStringConverter(leafCategories));

        productTypes = FXCollections.observableArrayList(Arrays.asList(ProductType.SELLABLE, ProductType.PARTIALLY_PAYABLE, ProductType.STORABLE));
        type.setItems(productTypes);
        type.setConverter(new ProductTypeStringConverter(productTypes));

        productStatuses = FXCollections.observableArrayList(Arrays.asList(ProductStatus.values()));
        status.setItems(productStatuses);
        status.setConverter(new ProductStatusStringConverter(productStatuses));

        quantityUnits = FXCollections.observableArrayList(Arrays.asList(QuantityUnit.values()));
        quantityUnit.setItems(quantityUnits);
        quantityUnit.setConverter(new QuantityUnitStringConverter(quantityUnits));
    }

    @Override
    public void loadProductForm(GoodsController goodsController) {
        this.goodsController = goodsController;
        productId = 0L;
        longName.clear();
        shortName.clear();
        rapidCode.clear();
        storageMultiplier.clear();
        salePrice.clear();
        purchasePrice.clear();
        minimumStock.clear();
        stockWindow.clear();
        type.setValue(null);
        status.setValue(null);
        quantityUnit.setValue(null);
        category.setValue(null);
    }

    @Override
    public void setProductViewModel(ProductViewModel productViewModel) {
        productId = productViewModel.getId();
        longName.setText(productViewModel.getLongName());
        shortName.setText(productViewModel.getShortName());
        rapidCode.setText(productViewModel.getRapidCode());
        storageMultiplier.setText(productViewModel.getStorageMultiplier());
        salePrice.setText(productViewModel.getSalePrice());
        purchasePrice.setText(productViewModel.getPurchasePrice());
        minimumStock.setText(productViewModel.getMinimumStock());
        stockWindow.setText(productViewModel.getStockWindow());
        type.setValue(type.getConverter().fromString(productViewModel.getType()));
        status.setValue(status.getConverter().fromString(productViewModel.getStatus()));
        quantityUnit.setValue(quantityUnit.getConverter().fromString(productViewModel.getQuantityUnit()));
    }

    @Override
    public void setCategory(CategoryViewModel categoryViewModel) {
        category.setValue(category.getConverter().fromString(categoryViewModel.getName()));
    }

    @FXML
    public void onConfirm(Event event) {
        try {
            goodsController.addProduct(productId, category.getValue(), commonService.productBuilder()
                .longName(longName.getText())
                .shortName(shortName.getText().equals("") ? longName.getText() : shortName.getText())
                .type(type.getValue())
                .status(status.getValue())
                .rapidCode(Integer.valueOf(rapidCode.getText()))
                .quantityUnit(quantityUnit.getValue())
                .storageMultiplier(Double.valueOf(storageMultiplier.getText()))
                .purchasePrice(Integer.valueOf(purchasePrice.getText()))
                .salePrice(Integer.valueOf(salePrice.getText()))
                .minimumStock(Integer.valueOf(minimumStock.getText()))
                .stockWindow(Integer.valueOf(stockWindow.getText())));
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }

    private class ProductTypeStringConverter extends StringConverter<ProductType> {
        private ObservableList<ProductType> productTypes;

        public ProductTypeStringConverter(ObservableList<ProductType> productTypes) {
            this.productTypes = productTypes;
        }

        @Override
        public String toString(ProductType object) {
            return object.toString();
        }

        @Override
        public ProductType fromString(String string) {
            return productTypes.stream().filter(productType -> productType.toString().equals(string))
                    .collect(toList()).get(0);
        }
    }

    private class ProductStatusStringConverter extends StringConverter<ProductStatus> {
        private ObservableList<ProductStatus> productStatus;

        public ProductStatusStringConverter(ObservableList<ProductStatus> ProductStatuss) {
            this.productStatus = ProductStatuss;
        }

        @Override
        public String toString(ProductStatus object) {
            return object.toString();
        }

        @Override
        public ProductStatus fromString(String string) {
            return productStatus.stream().filter(productStatus -> productStatus.toString().equals(string))
                    .collect(toList()).get(0);
        }
    }

    private class QuantityUnitStringConverter extends StringConverter<QuantityUnit> {
        private ObservableList<QuantityUnit> quantityUnit;

        public QuantityUnitStringConverter(ObservableList<QuantityUnit> quantityUnit) {
            this.quantityUnit = quantityUnit;
        }

        @Override
        public String toString(QuantityUnit object) {
            return object.toString();
        }

        @Override
        public QuantityUnit fromString(String string) {
            return quantityUnit.stream().filter(QuantityUnit -> QuantityUnit.toString().equals(string))
                    .collect(toList()).get(0);
        }
    }

}

