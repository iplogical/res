package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.DAILY;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.NO_REPETITION;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.WEEKLY;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType.QUANTITY_DISCOUNT;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType.SIMPLE_DISCOUNT;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.inject.Inject;

import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.ProductStringConverter;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
@Singleton
public class PriceModifierFormControllerImpl implements PriceModifierFormController {

    @FXML
    private VBox root;
    @FXML
    private TextField name;
    @FXML
    private ChoiceBox<ProductView> ownerProduct;
    @FXML
    private ChoiceBox<ProductCategoryView> ownerCategory;
    @FXML
    private CheckBox isCategory;
    @FXML
    private ChoiceBox<PriceModifierType> type;
    @FXML
    private TextField quantityMultiplier;
    @FXML
    private TextField discountPercent;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private ChoiceBox<PriceModifierRepeatPeriod> repeatPeriod;
    @FXML
    private TextField repeatPeriodMultiplier;
    @FXML
    private TextField startTimeHour;
    @FXML
    private TextField startTimeMinute;
    @FXML
    private TextField endTimeHour;
    @FXML
    private TextField endTimeMinute;
    @FXML
    private ChoiceBox<DayOfWeek> dayOfWeek;

    private PriceModifierController priceModifierController;

    @Inject
    private CommonService commonService;

    @Inject
    private ManagerService managerService;

    private BooleanProperty isCategorySelected;

    private ObservableList<ProductView> products;

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
        addDragAndDrop(root);
        isCategorySelected = isCategory.selectedProperty();
        products = FXCollections.observableArrayList(commonService.getSellableProducts());
        ownerProduct.setItems(products);
        ownerProduct.setConverter(new ProductStringConverter(products));
        productCategories = FXCollections.observableArrayList(commonService.getAllCategories());
        ownerCategory.setItems(productCategories);
        ownerCategory.setConverter(new CategoryStringConverter(productCategories));
        priceModifierTypes = FXCollections.observableArrayList(Arrays.asList(SIMPLE_DISCOUNT, QUANTITY_DISCOUNT));
        type.setItems(priceModifierTypes);
        priceModifierRepeatPeriods = FXCollections.observableArrayList(Arrays.asList(NO_REPETITION, DAILY, WEEKLY));
        repeatPeriod.setItems(priceModifierRepeatPeriods);
        dayOfWeek.setItems(FXCollections.observableArrayList(Arrays.asList(DayOfWeek.values())));
    }

    @Override
    public void loadPriceModifierForm(PriceModifierController priceModifierController) {
        this.priceModifierController = priceModifierController;
        originalName = "";
    }

    @FXML
    public void onConfirm(Event event) {
        PriceModifier.PriceModifierBuilder builder = managerService.priceModifierBuilder()
                .name(name.getText())
                .type(type.getValue())
                .quantityLimit(Integer.valueOf(quantityMultiplier.getText()))
                .discountPercent(Double.valueOf(discountPercent.getText()))
                .startDate(LocalDateTime.of(startDate.getValue(), LocalTime.of(0, 0)))
                .endDate(LocalDateTime.of(endDate.getValue(), LocalTime.of(23, 59)))
                .repeatPeriod(repeatPeriod.getValue())
                .repeatPeriodMultiplier(Integer.valueOf(repeatPeriodMultiplier.getText()))
                .startTime(LocalTime.of(Integer.valueOf(startTimeHour.getText()), Integer.valueOf(startTimeMinute.getText())))
                .endTime(LocalTime.of(Integer.valueOf(endTimeHour.getText()), Integer.valueOf(endTimeMinute.getText())))
                .dayOfWeek(dayOfWeek.getValue());

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
