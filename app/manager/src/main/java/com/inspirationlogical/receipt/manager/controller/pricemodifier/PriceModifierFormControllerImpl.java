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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.ValidationResult;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd.");

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
        initDateConverters();
        initRepeatPeriod();
        initDayOfWeek();
    }

    private void initDateConverters() {
        startDate.setConverter(new DateStringConverter());
        endDate.setConverter(new DateStringConverter());
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
        ownerProduct.setValue(null);
        ownerCategory.setValue(null);
        isCategory.setSelected(false);
        type.setValue(null);
        quantityLimit.setText("");
        discountPercent.setText("");
        startDate.setValue(null);
        endDate.setValue(null);
        repeatPeriod.setValue(null);
        startTimeHour.setText("");
        startTimeMinute.setText("");
        endTimeHour.setText("");
        endTimeMinute.setText("");
        dayOfWeek.setValue(null);
    }

    @FXML
    public void onConfirm(Event event) {
        ValidationResult validationResult = new InputValidator().validate();
        if(!validationResult.isValid()) {
            ErrorMessage.showErrorMessage(root, validationResult.getErrorMessage());
            return;
        }
        PriceModifier.PriceModifierBuilder builder = getPriceModifierBuilder();
        PriceModifierParams params = getPriceModifierParams(builder);
        priceModifierController.addPriceModifier(params);
    }

    private PriceModifier.PriceModifierBuilder getPriceModifierBuilder() {
        String quantityLimit = getQuantityLimitValue();
        LocalTime startTime = getStartTime();
        LocalTime endTime = getEndTime();

        return managerService.priceModifierBuilder()
                    .name(name.getText())
                    .type(type.getValue())
                    .quantityLimit(Integer.valueOf(quantityLimit))
                    .discountPercent(Double.valueOf(discountPercent.getText()))
                    .startDate(LocalDateTime.of(getStartDate(), LocalTime.of(0, 0)))
                    .endDate(LocalDateTime.of(getEndDate(), LocalTime.of(23, 59)))
                    .repeatPeriod(repeatPeriod.getValue())
                    .startTime(startTime)
                    .endTime(endTime)
                    .dayOfWeek(dayOfWeek.getValue());
    }

    private String getQuantityLimitValue() {
        String quantityLimit;
        if(!type.getValue().equals(QUANTITY_DISCOUNT)) {
            quantityLimit = "0";
        } else {
            quantityLimit = this.quantityLimit.getText();
        }
        return quantityLimit;
    }

    private LocalTime getStartTime() {
        if(repeatPeriod.getValue().equals(DAILY)) {
            return LocalTime.of(Integer.valueOf(startTimeHour.getText()), Integer.valueOf(startTimeMinute.getText()));
        }
        return null;
    }

    private LocalTime getEndTime() {
        if(repeatPeriod.getValue().equals(DAILY)) {
            return LocalTime.of(Integer.valueOf(endTimeHour.getText()), Integer.valueOf(endTimeMinute.getText()));
        }
        return null;
    }

    private LocalDate getStartDate() {
        return LocalDate.parse(startDate.getEditor().getText(), DATE_FORMATTER);
    }

    private LocalDate getEndDate() {
        return LocalDate.parse(endDate.getEditor().getText(), DATE_FORMATTER);
    }

    private PriceModifierParams getPriceModifierParams(PriceModifier.PriceModifierBuilder builder) {
        return PriceModifierParams.builder()
                .originalName(originalName)
                .ownerName(isCategorySelected.getValue() ? ownerCategory.getValue().getCategoryName() : ownerProduct.getValue().getLongName())
                .isCategory(isCategorySelected.getValue())
                .builder(builder)
                .build();
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

    private class DateStringConverter extends StringConverter<LocalDate> {
        @Override
        public String toString(LocalDate date) {
            if(date != null) {
                return date.format(DATE_FORMATTER);
            }
            return "";
        }

        @Override
        public LocalDate fromString(String string) {
            if(string != null) {
                LocalDate date = LocalDate.parse(string, DATE_FORMATTER);
                return date;
            }
            return null;
        }
    }

    private class InputValidator {
        private boolean valid = true;
        private StringBuilder errorMessage = new StringBuilder();

        private ValidationResult validate() {
            validateName();
            validateType();
            validateOwner();
            validateDisountPercent();
            ValidateDates();
            validateRepeatPeriod();
            return new ValidationResult(valid, errorMessage.toString());
        }

        private void validateName() {
            if(name.getText().isEmpty()) {
                valid = false;
                errorMessage.append(
                        ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyName")).append(System.lineSeparator());
            }
        }

        private void validateType() {
            if(type.getValue() == null) {
                valid = false;
                errorMessage.append(
                        ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyType")).append(System.lineSeparator());
                return;
            }
            validateQuantityLimit();
        }

        private void validateQuantityLimit() {
            if(type.getValue().equals(QUANTITY_DISCOUNT)) {
                try {
                    int limit = Integer.valueOf(quantityLimit.getText());
                } catch (NumberFormatException e) {
                    valid = false;
                    errorMessage.append(
                            ManagerResources.MANAGER.getString("PriceModifierForm.ErrorQuantityLimitFormat")).append(System.lineSeparator());
                 }
            }
        }

        private void validateOwner() {
            if(isCategory.selectedProperty().get()) {
                if(ownerCategory.getValue() == null) {
                    valid = false;
                    errorMessage.append(
                            ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyCategory")).append(System.lineSeparator());
                }
            } else {
                if(ownerProduct.getValue() == null) {
                    valid = false;
                    errorMessage.append(
                            ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyProduct")).append(System.lineSeparator());
                }
            }
        }

        private void validateDisountPercent() {
            try {
                double discount = Double.valueOf(discountPercent.getText());
            } catch (NumberFormatException e) {
                valid = false;
                errorMessage.append(
                        ManagerResources.MANAGER.getString("PriceModifierForm.ErrorDiscountFormat")).append(System.lineSeparator());
            }
        }

        private void ValidateDates() {
            LocalDate start = validateStartDate();
            LocalDate end = validateEndDate();
            validateStartIsBeforeEnd(start, end);
        }

        private LocalDate validateStartDate() {
            try {
                return getStartDate();
            } catch (DateTimeParseException e) {
                valid = false;
                errorMessage.append(
                        ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyStartDate")).append(System.lineSeparator());
            }
            return null;
        }

        private LocalDate validateEndDate() {
            try {
                return LocalDate.parse(endDate.getEditor().getText(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                valid = false;
                errorMessage.append(
                        ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyEndDate")).append(System.lineSeparator());
            }
            return null;
        }

        private void validateStartIsBeforeEnd(LocalDate start, LocalDate end) {
            if(start == null || end == null) {
                return;
            }
            if(start.isAfter(end)) {
                valid = false;
                errorMessage.append(
                        ManagerResources.MANAGER.getString("PriceModifierForm.ErrorStartDateAfterEndDate")).append(System.lineSeparator());
            }
        }

        private void validateRepeatPeriod() {
            if(repeatPeriod.getValue() == null) {
                valid = false;
                errorMessage.append(
                        ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyRepeatPeriod")).append(System.lineSeparator());
                return;
            }
            validateDailyRepeatPeriodTimeValues();
            validateWeeklyRepeatPeriodDayValue();
        }

        private void validateDailyRepeatPeriodTimeValues() {
            if(repeatPeriod.getValue().equals(DAILY)) {
                try {
                    int startHour = Integer.valueOf(startTimeHour.getText());
                    int startMinute = Integer.valueOf(startTimeMinute.getText());
                    int endHour = Integer.valueOf(endTimeHour.getText());
                    int endMinute = Integer.valueOf(endTimeMinute.getText());
                    if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23) {
                        valid = false;
                        errorMessage.append(
                                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorHoursRange")).append(System.lineSeparator());
                    }
                    if (startMinute < 0 || startMinute > 59 || endMinute < 0 || endMinute > 59) {
                        valid = false;
                        errorMessage.append(
                                ManagerResources.MANAGER.getString("PriceModifierForm.ErrorMinutesRange")).append(System.lineSeparator());
                    }
                } catch (NumberFormatException e) {
                    valid = false;
                    errorMessage.append(
                            ManagerResources.MANAGER.getString("PriceModifierForm.ErrorTimesFormat")).append(System.lineSeparator());
                }
            }
        }

        private void validateWeeklyRepeatPeriodDayValue() {
            if (repeatPeriod.getValue().equals(WEEKLY)) {
                if(dayOfWeek.getValue() == null) {
                    valid = false;
                    errorMessage.append(ManagerResources.MANAGER.getString("PriceModifierForm.ErrorEmptyDay")).append(System.lineSeparator());
                }
            }
        }
    }
}
