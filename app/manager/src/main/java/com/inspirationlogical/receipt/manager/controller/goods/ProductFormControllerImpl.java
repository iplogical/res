package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static java.util.stream.Collectors.toList;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
@Singleton
public class ProductFormControllerImpl implements ProductFormController {

    @FXML
    private VBox root;
    @FXML
    private TextField longName;
    @FXML
    private TextField shortName;
    @FXML
    private ChoiceBox<ProductType> type;
    @FXML
    private ChoiceBox<ProductCategoryView> category;
    @FXML
    private ChoiceBox<ProductStatus> status;
    @FXML
    private TextField rapidCode;
    @FXML
    private ChoiceBox<QuantityUnit> quantityUnit;
    @FXML
    private TextField storageMultiplier;
    @FXML
    private TextField salePrice;
    @FXML
    private TextField purchasePrice;
    @FXML
    private TextField minimumStock;
    @FXML
    private TextField stockWindow;

    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    private Long productId;

    @Override
    public String getViewPath() {
        return "/view/fxml/ProductForm.fxml";
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root);
        initChoiceBoxes();
    }

    private void initChoiceBoxes() {
        initCategoryChoiceBox();
        initProductTypeChoiceBox();
        initProductStatusChoiceBox();
        initQuantityUnitChoiceBox();
    }

    private void initCategoryChoiceBox() {
        ObservableList<ProductCategoryView> leafCategories = FXCollections.observableArrayList(commonService.getLeafCategories());
        leafCategories.sort(Comparator.comparing(ProductCategoryView::getCategoryName));
        category.setItems(leafCategories);
        category.setConverter(new CategoryStringConverter(leafCategories));
    }

    private void initProductTypeChoiceBox() {
        ObservableList<ProductType> productTypes = FXCollections.observableArrayList(Arrays.asList(ProductType.SELLABLE, ProductType.PARTIALLY_PAYABLE, ProductType.STORABLE));
        productTypes.sort(Comparator.comparing(ProductType::toI18nString));
        type.setItems(productTypes);
        type.setConverter(new ProductTypeStringConverter(productTypes));
    }

    private void initProductStatusChoiceBox() {
        ObservableList<ProductStatus> productStatuses = FXCollections.observableArrayList(Arrays.asList(ProductStatus.values()));
        productStatuses.sort(Comparator.comparing(ProductStatus::toI18nString));
        status.setItems(productStatuses);
        status.setConverter(new ProductStatusStringConverter(productStatuses));
    }

    private void initQuantityUnitChoiceBox() {
        ObservableList<QuantityUnit> quantityUnits = FXCollections.observableArrayList(Arrays.asList(QuantityUnit.values()));
        quantityUnits.sort(Comparator.comparing(QuantityUnit::toI18nString));
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
        minimumStock.setText("7");
        stockWindow.setText("60");
        type.setValue(ProductType.SELLABLE);
        status.setValue(ProductStatus.ACTIVE);
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
            goodsController.addProduct(productId, category.getValue(), buildProduct());
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(getRootNode(),
                    Resources.MANAGER.getString("ProductForm.NumberFormatException"));
        }
    }

    private Product.ProductBuilder buildProduct() throws NumberFormatException {
        return commonService.productBuilder()
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
            .stockWindow(Integer.valueOf(stockWindow.getText()));
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }

    private class ProductTypeStringConverter extends StringConverter<ProductType> {
        private ObservableList<ProductType> productTypes;

        ProductTypeStringConverter(ObservableList<ProductType> productTypes) {
            this.productTypes = productTypes;
        }

        @Override
        public String toString(ProductType productType) {
            return productType.toI18nString();
        }

        @Override
        public ProductType fromString(String string) {
            return productTypes.stream().filter(productType -> productType.toString().equals(string))
                    .collect(toList()).get(0);
        }
    }

    private class ProductStatusStringConverter extends StringConverter<ProductStatus> {
        private ObservableList<ProductStatus> productStatus;

        ProductStatusStringConverter(ObservableList<ProductStatus> productStatus) {
            this.productStatus = productStatus;
        }

        @Override
        public String toString(ProductStatus productStatus) {
            return productStatus.toI18nString();
        }

        @Override
        public ProductStatus fromString(String string) {
            return productStatus.stream().filter(productStatus -> productStatus.toString().equals(string))
                    .collect(toList()).get(0);
        }
    }

    private class QuantityUnitStringConverter extends StringConverter<QuantityUnit> {
        private ObservableList<QuantityUnit> quantityUnit;

        QuantityUnitStringConverter(ObservableList<QuantityUnit> quantityUnit) {
            this.quantityUnit = quantityUnit;
        }

        @Override
        public String toString(QuantityUnit quantityUnit) {
            return quantityUnit.toI18nString();
        }

        @Override
        public QuantityUnit fromString(String string) {
            return quantityUnit.stream().filter(QuantityUnit -> QuantityUnit.toString().equals(string))
                    .collect(toList()).get(0);
        }
    }

}

