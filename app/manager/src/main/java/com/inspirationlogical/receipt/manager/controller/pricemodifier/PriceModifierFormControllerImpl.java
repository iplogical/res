package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.DAILY;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.NO_REPETITION;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod.WEEKLY;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType.QUANTITY_DISCOUNT;
import static com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType.SIMPLE_DISCOUNT;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
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
import com.inspirationlogical.receipt.manager.viewmodel.PriceModifierViewModel;
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
import javafx.util.StringConverter;

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
    private TextField quantityLimit;
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

    private PriceModifierViewModel priceModifierViewModel;

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
        initOwnerProduct();
        initOwnerCategory();
        initPriceModifierType();
        initRepeatPeriod();
        initDayOfWeek();
    }

    private void initOwnerProduct() {
        products = FXCollections.observableArrayList(commonService.getSellableProducts());
        products.sort(Comparator.comparing(ProductView::getShortName));
        ownerProduct.setItems(products);
        ownerProduct.setConverter(new ProductStringConverter(products));
    }

    private void initOwnerCategory() {
        isCategorySelected = isCategory.selectedProperty();
        productCategories = FXCollections.observableArrayList(commonService.getAggregateCategories());
        productCategories.addAll(commonService.getLeafCategories());
        productCategories.sort(Comparator.comparing(ProductCategoryView::getCategoryName));
        ownerCategory.setItems(productCategories);
        ownerCategory.setConverter(new CategoryStringConverter(productCategories));
    }

    private void initPriceModifierType() {
        priceModifierTypes = FXCollections.observableArrayList(Arrays.asList(SIMPLE_DISCOUNT, QUANTITY_DISCOUNT));
        priceModifierTypes.sort(Comparator.comparing(PriceModifierType::toI18nString));
        type.setItems(priceModifierTypes);
        type.setConverter(new PriceModifierTypeStringConverter(priceModifierTypes));
    }

    private void initRepeatPeriod() {
        priceModifierRepeatPeriods = FXCollections.observableArrayList(Arrays.asList(NO_REPETITION, DAILY, WEEKLY));
        priceModifierRepeatPeriods.sort(Comparator.comparing(PriceModifierRepeatPeriod::toI18nString));
        repeatPeriod.setItems(priceModifierRepeatPeriods);
        repeatPeriod.setConverter(new PriceModifierRepeatPeriodStringConverter(priceModifierRepeatPeriods));
    }

    private void initDayOfWeek() {
        ObservableList<DayOfWeek> days = FXCollections.observableArrayList(DayOfWeek.values());
        dayOfWeek.setItems(FXCollections.observableArrayList(Arrays.asList(DayOfWeek.values())));
        dayOfWeek.setConverter(new DayOfWeekStringConverter(days));
    }

    @Override
    public void loadPriceModifierForm(PriceModifierController priceModifierController) {
        this.priceModifierController = priceModifierController;
        originalName = "";
    }

    @Override
    public void loadPriceModifierForm(PriceModifierViewModel selected) {
        this.priceModifierViewModel = selected;
        setOwner();
        originalName = priceModifierViewModel.getName();
        name.setText(priceModifierViewModel.getName());
        type.setValue(type.getConverter().fromString(priceModifierViewModel.getType()));
        quantityLimit.setText(priceModifierViewModel.getQuantityLimit());
        discountPercent.setText(priceModifierViewModel.getDiscountPercent());
        LocalDateTime startDateParsed = LocalDateTime.parse(priceModifierViewModel.getStartDate());
        startDate.setValue(LocalDate.of(startDateParsed.getYear(), startDateParsed.getMonth(), startDateParsed.getDayOfMonth()));
        LocalDateTime endDateParsed = LocalDateTime.parse(priceModifierViewModel.getEndDate());
        endDate.setValue(LocalDate.of(endDateParsed.getYear(), endDateParsed.getMonth(), endDateParsed.getDayOfMonth()));
        repeatPeriod.setValue(repeatPeriod.getConverter().fromString(priceModifierViewModel.getRepeatPeriod()));
        repeatPeriodMultiplier.setText(priceModifierViewModel.getPeriodMultiplier());
        setStartTime();
        setEndTime();
        dayOfWeek.setValue(dayOfWeek.getConverter().fromString(priceModifierViewModel.getDayOfWeek()));
    }

    private void setOwner() {
        ProductView ownerProductView = ((ProductStringConverter)ownerProduct.getConverter()).fromLongNameString(priceModifierViewModel.getOwnerName());
        if(ownerProductView != null) {
            ownerProduct.setValue(ownerProductView);
        } else {
            ProductCategoryView ownerCategoryView = ownerCategory.getConverter().fromString(priceModifierViewModel.getOwnerName());
            ownerCategory.setValue(ownerCategoryView);
            isCategory.setSelected(true);
        }
    }

    private void setStartTime() {
        if (!priceModifierViewModel.getStartTime().equals("")) {
            LocalTime startTime = LocalTime.parse(priceModifierViewModel.getStartTime());
            startTimeHour.setText(String.valueOf(startTime.getHour()));
            startTimeMinute.setText(String.valueOf(startTime.getMinute()));
        }
    }

    private void setEndTime() {
        if (!priceModifierViewModel.getEndTime().equals("")) {
            LocalTime endTime = LocalTime.parse(priceModifierViewModel.getEndTime());
            endTimeHour.setText(String.valueOf(endTime.getHour()));
            endTimeMinute.setText(String.valueOf(endTime.getMinute()));
        }
    }

    @Override
    public void clearPriceModifierForm() {
        originalName = "";
        name.setText("");
        type.setValue(null);
        quantityLimit.setText("");
        discountPercent.setText("");
        startDate.setValue(null);
        endDate.setValue(null);
        repeatPeriod.setValue(null);
        repeatPeriodMultiplier.setText("");
        startTimeHour.setText("");
        startTimeMinute.setText("");
        endTimeHour.setText("");
        endTimeMinute.setText("");
        dayOfWeek.setValue(null);
    }

    @FXML
    public void onConfirm(Event event) {
        PriceModifier.PriceModifierBuilder builder = managerService.priceModifierBuilder()
                .name(name.getText())
                .type(type.getValue())
                .quantityLimit(Integer.valueOf(quantityLimit.getText()))
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

    private class PriceModifierTypeStringConverter extends StringConverter<PriceModifierType> {
        private ObservableList<PriceModifierType> priceModifierTypes;

        PriceModifierTypeStringConverter(ObservableList<PriceModifierType> priceModifierTypes) {
            this.priceModifierTypes = priceModifierTypes;
        }

        @Override
        public String toString(PriceModifierType priceModifierType) {
            return priceModifierType.toI18nString();
        }

        @Override
        public PriceModifierType fromString(String string) {
            return priceModifierTypes.stream().filter(priceModifierType -> priceModifierType.toI18nString().equals(string))
                    .collect(toList()).get(0);
        }
    }

    private class PriceModifierRepeatPeriodStringConverter extends StringConverter<PriceModifierRepeatPeriod> {
        private ObservableList<PriceModifierRepeatPeriod> priceModifierRepeatPeriods;

        public PriceModifierRepeatPeriodStringConverter(ObservableList<PriceModifierRepeatPeriod> priceModifierRepeatPeriods) {
            this.priceModifierRepeatPeriods = priceModifierRepeatPeriods;
        }

        @Override
        public String toString(PriceModifierRepeatPeriod priceModifierRepeatPeriod) {
            return priceModifierRepeatPeriod.toI18nString();
        }

        @Override
        public PriceModifierRepeatPeriod fromString(String string) {
            return priceModifierRepeatPeriods.stream()
                    .filter(priceModifierRepeatPeriod -> priceModifierRepeatPeriod.toI18nString().equals(string))
                    .collect(toList()).get(0);
        }
    }

    private class DayOfWeekStringConverter extends StringConverter<DayOfWeek> {
        private ObservableList<DayOfWeek> dayOfWeeks;

        public DayOfWeekStringConverter(ObservableList<DayOfWeek> dayOfWeeks) {
            this.dayOfWeeks = dayOfWeeks;
        }

        @Override
        public String toString(DayOfWeek dayOfWeek) {
            return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("hu"));
        }

        @Override
        public DayOfWeek fromString(String string) {
            return dayOfWeeks.stream().filter(dayOfWeek1 -> dayOfWeek1.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("hu")).equals(string))
                    .collect(toList()).get(0);
        }
    }
}
