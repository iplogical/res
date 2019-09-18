package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.utility.NotificationMessage;
import com.inspirationlogical.receipt.manager.exception.InvalidInputFormException;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.ProductStatusStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.VatNameStringConverter;
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

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addFormDragAndDrop;
import static java.util.stream.Collectors.toList;

/**
 * Created by régiDAGi on 2017. 04. 10..
 */
@FXMLController
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
    private ChoiceBox<VATName> vat;
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
    @FXML
    private TextField orderNumber;

    private GoodsController goodsController;

    @Autowired
    private CommonService commonService;

    private int productId;

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addFormDragAndDrop(root);
        initChoiceBoxes();
    }

    private void initChoiceBoxes() {
        initCategoryChoiceBox();
        initProductTypeChoiceBox();
        initProductStatusChoiceBox();
        initProductVatChoiceBox();
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

    private void initProductVatChoiceBox() {
        ObservableList<VATName> vatNameList = FXCollections.observableArrayList(Arrays.asList(VATName.NORMAL, VATName.GREATLY_REDUCED));
        vatNameList.sort(Comparator.comparing(VATName::toI18nString));
        vat.setItems(vatNameList);
        vat.setConverter(new VatNameStringConverter(vatNameList));
    }

    private void initQuantityUnitChoiceBox() {
        ObservableList<QuantityUnit> quantityUnits = FXCollections.observableArrayList(Arrays.asList(QuantityUnit.values()));
        quantityUnits.sort(Comparator.comparing(QuantityUnit::toI18nString));
        quantityUnit.setItems(quantityUnits);
        quantityUnit.setConverter(new QuantityUnitStringConverter(quantityUnits));
    }

    @Override
    public void loadProductForm(GoodsController goodsController) {
        initCategoryChoiceBox();
        this.goodsController = goodsController;
        productId = 0;
        longName.clear();
        shortName.clear();
        type.setValue(ProductType.SELLABLE);
        category.setValue(null);
        status.setValue(ProductStatus.ACTIVE);
        vat.setValue(VATName.NORMAL);
        quantityUnit.setValue(null);
        storageMultiplier.clear();
        salePrice.clear();
        purchasePrice.clear();
        minimumStock.setText("7");
        stockWindow.setText("60");
        orderNumber.setText("0");
        rapidCode.setText("0");
    }

    @Override
    public void setProductViewModel(ProductView productView) {
        productId = productView.getId();
        longName.setText(productView.getLongName());
        shortName.setText(productView.getShortName());
        type.setValue(type.getConverter().fromString(productView.getType().toI18nString()));
        status.setValue(status.getConverter().fromString(productView.getStatus().toI18nString()));
        vat.setValue(vat.getConverter().fromString(productView.getVat().toI18nString()));
        storageMultiplier.setText(String.valueOf(productView.getStorageMultiplier()));
        salePrice.setText(String.valueOf(productView.getSalePrice()));
        purchasePrice.setText(String.valueOf(productView.getPurchasePrice()));
        minimumStock.setText(String.valueOf(productView.getMinimumStock()));
        stockWindow.setText(String.valueOf(productView.getStockWindow()));
        orderNumber.setText(String.valueOf(productView.getOrderNumber()));
        quantityUnit.setValue(quantityUnit.getConverter().fromString(productView.getQuantityUnit().toI18nString()));
        rapidCode.setText(String.valueOf(productView.getRapidCode()));
    }

    @Override
    public void setCategory(ProductCategoryView categoryViewModel) {
        category.setValue(category.getConverter().fromString(categoryViewModel.getName()));
    }

    @FXML
    public void onConfirm(Event event) {
        try {
            goodsController.addProduct(buildProductView(), category.getValue());
        } catch (NumberFormatException e) {
            NotificationMessage.showErrorMessage(getRootNode(),
                    ManagerResources.MANAGER.getString("Form.NumberFormatException"));
        } catch (InvalidInputFormException e) {
            NotificationMessage.showErrorMessage(getRootNode(),
                    ManagerResources.MANAGER.getString("Form.EmptyNameOrChoiceBox"));
        }
    }

    private ProductView buildProductView() throws NumberFormatException, InvalidInputFormException {
        if (isChoiceBoxEmpty() || isLongNameEmpty())
            throw new InvalidInputFormException("");
        return ProductView.builder()
                .id(productId)
                .longName(longName.getText())
                .shortName(shortName.getText().equals("") ? longName.getText() : shortName.getText())
                .type(type.getValue())
                .status(status.getValue())
                .vat(vat.getValue())
                .rapidCode(Integer.parseInt(rapidCode.getText()))
                .quantityUnit(quantityUnit.getValue())
                .storageMultiplier(Double.parseDouble(storageMultiplier.getText()))
                .purchasePrice(Integer.parseInt(purchasePrice.getText()))
                .salePrice(Integer.parseInt(salePrice.getText()))
                .minimumStock(Integer.parseInt(minimumStock.getText()))
                .stockWindow(Integer.parseInt(stockWindow.getText()))
                .orderNumber(Integer.parseInt(orderNumber.getText()))
                .build();
    }

    private boolean isChoiceBoxEmpty() {
        return type.getValue() == null || category.getValue() == null || status.getValue() == null || quantityUnit.getValue() == null
                || vat.getValue() == null;
    }

    private boolean isLongNameEmpty() {
        return longName.getText().isEmpty();
    }

    @FXML
    public void onCancel(Event event) {
        goodsController.hideProductForm();
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
            return productTypes.stream().filter(productType -> productType.toI18nString().equals(string))
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
            return quantityUnit.stream().filter(QuantityUnit -> QuantityUnit.toI18nString().equals(string))
                    .collect(toList()).get(0);
        }
    }

}

