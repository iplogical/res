package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reservation.CalendarPickerWrapper;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javax.inject.Singleton;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by TheDagi on 2017. 04. 21..
 */
@Singleton
public class DailySummaryControllerImpl extends AbstractRetailControllerImpl
    implements DailySummaryController {

    private static final String DAILY_SUMMARY_VIEW_PATH = "/view/fxml/DailySummary.fxml";

    @FXML
    private BorderPane root;

    @FXML
    private Label openConsumption;
    @FXML
    private Label cashTotalPrice;
    @FXML
    private Label creditCardTotalPrice;
    @FXML
    private Label couponTotalPrice;
    @FXML
    private Label totalPrice;

    @FXML
    private Label startDateValue;
    @FXML
    private Label endDateValue;

    @FXML
    private HBox startDateContainer;
    @FXML
    private HBox endDateContainer;

    @FXML
    private Button printDailyConsumption;

    @FXML
    private Label liveTime;

    private CalendarPickerWrapper startDatePicker;

    private CalendarPickerWrapper endDatePicker;

    private RestaurantView restaurantView;

    private String openConsumptionString;

    @Override
    public String getViewPath() {
        return DAILY_SUMMARY_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDate();
        initLiveTime(liveTime);
        updatePriceFields();
    }

    private void initDate() {
        initStartDate();
        initEndDate();
        updateClosureTimeLabels();
    }

    private void initStartDate() {
        startDatePicker = CalendarPickerWrapper.getInstance();
        startDatePicker.setOnChange(this::updateClosureTimeLabels);
        startDateContainer.getChildren().add(startDatePicker.getDate());
    }

    private void initEndDate() {
        endDatePicker = CalendarPickerWrapper.getInstance();
        endDatePicker.setOnChange(this::updateClosureTimeLabels);
        endDateContainer.getChildren().add(endDatePicker.getDate());
    }

    private void updateClosureTimeLabels() {
        List<LocalDateTime> closureTimes = retailService.getClosureTimes(startDatePicker.getSelectedDate(), endDatePicker.getSelectedDate());
        startDateValue.setText(closureTimes.get(0).toString());
        endDateValue.setText(closureTimes.get(1).toString());
    }

    @Override
    protected void soldProductsRowClickHandler(SoldProductViewModel row) {}

    @Override
    public void onBackToRestaurantView(Event event) {
        viewLoader.loadViewIntoScene(restaurantController);
    }

    @Override
    public void setRestaurantView(RestaurantView restaurantView) {
        this.restaurantView = restaurantView;
    }

    @Override
    public void setOpenConsumption(String openConsumption) {
        openConsumptionString = openConsumption;
    }

    @Override
    public void updatePriceFields() {
        cashTotalPrice.setText(String.valueOf(restaurantView.getConsumptionOfTheDay(PaymentMethod.CASH)));
        creditCardTotalPrice.setText(String.valueOf(restaurantView.getConsumptionOfTheDay(PaymentMethod.CREDIT_CARD)));
        couponTotalPrice.setText(String.valueOf(restaurantView.getConsumptionOfTheDay(PaymentMethod.COUPON)));
        openConsumption.setText(openConsumptionString);
        int totalPriceInt  = Integer.valueOf(openConsumption.getText()) +
                + Integer.valueOf(cashTotalPrice.getText())
                + Integer.valueOf(creditCardTotalPrice.getText())
                + Integer.valueOf(couponTotalPrice.getText());
        totalPrice.setText(String.valueOf(totalPriceInt));
    }

    @FXML
    public void onPrintDailyConsumption(Event event) {
        retailService.printAggregateConsumption(restaurantView, startDatePicker.getSelectedDate(), endDatePicker.getSelectedDate());
    }
}
