package com.inspirationlogical.receipt.manager.controller;

import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier.PriceModifierBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.*;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType.*;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.PriceModifierParams;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.ProductStringConverter;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.NO_REPETITION;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
@Singleton
public class PriceModifierFormControllerImpl implements PriceModifierFormController {

    @FXML
    VBox root;
    @FXML
    TextField name;
    @FXML
    ChoiceBox<ProductView> ownerProduct;
    @FXML
    ChoiceBox<ProductCategoryView> ownerCategory;
    @FXML
    CheckBox isCategory;
    @FXML
    ChoiceBox<PriceModifierType> type;
    @FXML
    TextField quantityMultiplier;
    @FXML
    TextField discountPercent;
    @FXML
    DatePicker startDate;
    @FXML
    DatePicker endDate;
    @FXML
    ChoiceBox<PriceModifierRepeatPeriod> repeatPeriod;
    @FXML
    TextField repeatPeriodMultiplier;

    private PriceModifierController priceModifierController;

    @Inject
    private CommonService commonService;

    private BooleanProperty isCategorySelected;

    private ObservableList<ProductView> products;

    private ProductCategoryView rootCategory;

    private ObservableList<ProductCategoryView> productCategories;

    private ObservableList<PriceModifierType> priceModifierTypes;

    private ObservableList<PriceModifierRepeatPeriod> priceModifierRepeatPeriods;

    private String originalName;

    public static String PRICE_MODIFIER_FORM_VIEW_PATH =  "/view/fxml/PriceModifierForm.fxml";

    @Override
    public String getViewPath() {
        return PRICE_MODIFIER_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isCategorySelected = isCategory.selectedProperty();
        rootCategory = commonService.getRootProductCategory();
        products = FXCollections.observableArrayList(commonService.getSellableProducts(rootCategory));
        ownerProduct.setItems(products);
        ownerProduct.setConverter(new ProductStringConverter(products));
        productCategories = FXCollections.observableArrayList(commonService.getAllCategories());
        ownerCategory.setItems(productCategories);
        ownerCategory.setConverter(new CategoryStringConverter(productCategories));
        priceModifierTypes = FXCollections.observableArrayList(Arrays.asList(SIMPLE_DISCOUNT, QUANTITY_DISCOUNT));
        type.setItems(priceModifierTypes);
        priceModifierRepeatPeriods = FXCollections.observableArrayList(Arrays.asList(NO_REPETITION, DAILY, WEEKLY));
        repeatPeriod.setItems(priceModifierRepeatPeriods);
    }

    @Override
    public void loadPriceModifierForm(PriceModifierController priceModifierController) {
        this.priceModifierController = priceModifierController;
        originalName = "";
    }

    @FXML
    public void onConfirm(Event event) {
        PriceModifier.PriceModifierBuilder builder = commonService.priceModifierBuilder()
                .name(name.getText())
                .type(type.getValue())
                .quantityLimit(Integer.valueOf(quantityMultiplier.getText()))
                .discountPercent(Double.valueOf(discountPercent.getText()))
                .startTime(LocalDateTime.of(startDate.getValue(), LocalTime.of(0, 0)))
                .endTime(LocalDateTime.of(startDate.getValue(), LocalTime.of(23, 59)))
                .repeatPeriod(repeatPeriod.getValue())
                .repeatPeriodMultiplier(Integer.valueOf(repeatPeriodMultiplier.getText()));
        PriceModifierParams params = PriceModifierParams.builder()
                .originalName(originalName)
                .ownerName(isCategorySelected.getValue() ? ownerCategory.getValue().getCategoryName() : ownerProduct.getValue().getLongName())
                .isCategory(isCategorySelected.getValue())
                .builder(builder)
                .build();
        priceModifierController.addPriceModifier(params);
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }
}
