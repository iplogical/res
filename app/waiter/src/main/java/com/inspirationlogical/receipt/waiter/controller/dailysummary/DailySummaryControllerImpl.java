package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyConsumptionService;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reservation.CalendarPickerWrapper;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.viewmodel.ProductRowModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@FXMLController
public class DailySummaryControllerImpl extends AbstractRetailControllerImpl
    implements DailySummaryController {

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
    private Label dailySummaryTotalPrice;

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

    @Autowired
    private DailyConsumptionService dailyConsumptionService;

    @Autowired
    private DailyClosureService dailyClosureService;

    private CalendarPickerWrapper startDatePicker;

    private CalendarPickerWrapper endDatePicker;

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSoldProducts();
        initDate();
        initLiveTime(liveTime);
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
        List<LocalDateTime> closureTimes = dailyClosureService.getClosureTimes(startDatePicker.getSelectedDate(), endDatePicker.getSelectedDate());
        startDateValue.setText(closureTimes.get(0).toString());
        endDateValue.setText(closureTimes.get(1).toString());
        getSoldProductsAndRefreshTable();
    }

    @Override
    protected Collection<ReceiptRecordView> getSoldProducts() {
        receiptView = dailyConsumptionService.getAggregatedReceipt(startDatePicker.getSelectedDate(), endDatePicker.getSelectedDate());
        return receiptView.getSoldProducts();
    }

    @Override
    protected void soldProductsRowClickHandler(ProductRowModel row) {}

    @Override
    public void onBackToRestaurantView(Event event) {
        WaiterApp.showView(RestaurantFxmlView.class);
    }

    @Override
    public void updatePriceFields() {
        Map<PaymentMethod, Integer> dailyConsumptionMap = dailyConsumptionService.getConsumptionOfTheDay();
        cashTotalPrice.setText(String.valueOf(dailyConsumptionMap.get(PaymentMethod.CASH)));
        creditCardTotalPrice.setText(String.valueOf(dailyConsumptionMap.get(PaymentMethod.CREDIT_CARD)));
        couponTotalPrice.setText(String.valueOf(dailyConsumptionMap.get(PaymentMethod.COUPON)));
        int openConsumptionValue = dailyConsumptionService.getOpenConsumption();
        openConsumption.setText(String.valueOf(openConsumptionValue));
        int totalPriceInt  = dailyConsumptionMap.values().stream().mapToInt(Integer::intValue).sum() + openConsumptionValue;
        dailySummaryTotalPrice.setText(String.valueOf(totalPriceInt));
        getSoldProductsAndRefreshTable();
    }

    @FXML
    public void onPrintDailyConsumption(Event event) {
        dailyConsumptionService.printAggregatedConsumption(startDatePicker.getSelectedDate(), endDatePicker.getSelectedDate());
    }
}
