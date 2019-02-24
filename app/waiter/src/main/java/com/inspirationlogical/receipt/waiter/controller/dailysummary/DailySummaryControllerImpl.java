package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRowModel;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyConsumptionService;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.viewmodel.ProductRowModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@FXMLController
public class DailySummaryControllerImpl extends AbstractController implements DailySummaryController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd.");

    @FXML
    private BorderPane root;

    @FXML
    private javafx.scene.control.TableView<ReceiptRowModel> receiptTable;
    private ObservableList<ReceiptRowModel> receiptRowList = FXCollections.observableArrayList();

    @FXML
    protected TableColumn<ReceiptRowModel, String> receiptId;
    @FXML
    private TableColumn<ReceiptRowModel, String> receiptTotalPrice;
    @FXML
    private TableColumn<ReceiptRowModel, String> receiptPaymentMethod;
    @FXML
    private TableColumn<ReceiptRowModel, String> receiptOpenTime;
    @FXML
    private TableColumn<ReceiptRowModel, String> receiptClosureTime;

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
    private Label productDiscount;

    @FXML
    private Label tableDiscount;

    @FXML
    private Label totalDiscount;

    @FXML
    private Label startDateValue;

    @FXML
    private TextField startDateTextField;
    private LocalDate startDate;

    @FXML
    private Label endDateValue;

    @FXML
    private TextField endDateTextField;
    private LocalDate endDate;

    @FXML
    private Button printDailyConsumption;

    @FXML
    private Label liveTime;

    @Autowired
    private DailyConsumptionService dailyConsumptionService;

    @Autowired
    private DailyClosureService dailyClosureService;

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeReceiptTable();
        initDate();
        initLiveTime(liveTime);
    }

    private void initializeReceiptTable() {
        receiptTable.setEditable(true);
        initColumn(receiptId, ReceiptRowModel::getReceiptId);
        initColumn(receiptTotalPrice, ReceiptRowModel::getReceiptTotalPrice);
        initColumn(receiptPaymentMethod, ReceiptRowModel::getReceiptPaymentMethod);
        initColumn(receiptOpenTime, ReceiptRowModel::getReceiptOpenTime);
        initColumn(receiptClosureTime, ReceiptRowModel::getReceiptClosureTime);
    }

    private void initDate() {
        initDateTextFields();
        updateClosureTimeLabels();
    }

    private void initDateTextFields() {
        startDate = LocalDate.now();
        startDateTextField.setText(DATE_FORMATTER.format(startDate));
        endDate = LocalDate.now();
        endDateTextField.setText(DATE_FORMATTER.format(endDate));
        startDateTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            startDate = LocalDate.parse(newValue, DATE_FORMATTER);
            updateClosureTimeLabels();
        });
        endDateTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            endDate = LocalDate.parse(newValue, DATE_FORMATTER);
            updateClosureTimeLabels();
        });
    }

    private void updateClosureTimeLabels() {
        List<LocalDateTime> closureTimes = dailyClosureService.getClosureTimes(startDate, endDate);
        startDateValue.setText(closureTimes.get(0).format(DATE_TIME_FORMATTER));
        endDateValue.setText(closureTimes.get(1).format(DATE_TIME_FORMATTER));
        enter();
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        WaiterApp.showView(RestaurantFxmlView.class);
    }

    @Override
    public void enter() {
        DailyConsumptionModel dailyConsumptionModel =
                dailyConsumptionService.getDailyConsumptionModel(startDate, endDate);
        cashTotalPrice.setText(String.valueOf(dailyConsumptionModel.getConsumptionCash()));
        creditCardTotalPrice.setText(String.valueOf(dailyConsumptionModel.getConsumptionCreditCard()));
        couponTotalPrice.setText(String.valueOf(dailyConsumptionModel.getConsumptionCoupon()));
        openConsumption.setText(String.valueOf(dailyConsumptionModel.getOpenConsumption()));
        dailySummaryTotalPrice.setText(String.valueOf(dailyConsumptionModel.getTotalConsumption()));
        productDiscount.setText(String.valueOf(dailyConsumptionModel.getProductDiscount()));
        tableDiscount.setText(String.valueOf(dailyConsumptionModel.getTableDiscount()));
        totalDiscount.setText(String.valueOf(dailyConsumptionModel.getTotalDiscount()));
        List<ReceiptRowModel> receiptRowModels = dailyConsumptionService.getReceipts(startDate, endDate);
        receiptRowList.clear();
        receiptRowList.addAll(receiptRowModels);
        receiptTable.setItems(receiptRowList);
        receiptTable.refresh();
    }

    @FXML
    public void onPrintDailyConsumption(Event event) {
        DailyConsumptionModel dailyConsumptionModel =
                dailyConsumptionService.getDailyConsumptionModel(startDate, endDate);
        dailyConsumptionService.printAggregatedConsumption(dailyConsumptionModel);
    }
}
