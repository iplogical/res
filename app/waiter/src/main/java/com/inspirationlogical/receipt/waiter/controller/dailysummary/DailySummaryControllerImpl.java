package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRowModel;
import com.inspirationlogical.receipt.corelib.params.CloseDayParams;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyConsumptionService;
import com.inspirationlogical.receipt.corelib.utility.NotificationMessage;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.waiter.utility.ConfirmMessage.showConfirmDialog;
import static java.util.stream.Collectors.toList;

@FXMLController
public class DailySummaryControllerImpl extends AbstractController implements DailySummaryController {

    final private static Logger logger = LoggerFactory.getLogger(DailySummaryControllerImpl.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM.dd. HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd.");

    @FXML
    private BorderPane root;

    @FXML
    private TableView<ReceiptRowModel> receiptTable;

    @FXML
    private TableColumn<ReceiptRowModel, String> receiptId;
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
    private Label serviceFeeTotal;
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
    private TextField totalCommerce;

    @FXML
    private TextField otherIncome;

    @FXML
    private TextField creditCardTerminal;

    @FXML
    private Label creditCardTerminalOver;

    @FXML
    private TextField serviceFeeExtra;

    @FXML
    private Button printDailyConsumption;

    @FXML
    private Button reloadButton;

    @FXML
    private Button closeDayButton;

    @FXML
    private ComboBox<PaymentMethod> paymentMethodCombo;

    @FXML
    private Button updatePaymentMethodButton;

    @FXML
    private Label liveTime;

    @Autowired
    private DailyConsumptionService dailyConsumptionService;

    @Autowired
    private DailyClosureService dailyClosureService;
    private DailyConsumptionModel dailyConsumptionModel;

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeReceiptTable();
        initDate();
        initLiveTime(liveTime);
        initPaymentMethodCombo();
    }

    private void initializeReceiptTable() {
        initColumn(receiptId, ReceiptRowModel::getReceiptId);
        initColumn(receiptTotalPrice, ReceiptRowModel::getReceiptTotalPrice);
        initColumn(receiptPaymentMethod, ReceiptRowModel::getReceiptPaymentMethod);
        initColumn(receiptOpenTime, ReceiptRowModel::getReceiptOpenTime);
        initColumn(receiptClosureTime, ReceiptRowModel::getReceiptClosureTime);
    }

    private void initDate() {
        initDateTextFields();
    }

    private void initDateTextFields() {
        startDate = LocalDate.now();
        startDateTextField.setText(DATE_FORMATTER.format(startDate));
        endDate = LocalDate.now();
        endDateTextField.setText(DATE_FORMATTER.format(endDate));
    }

    private void updateClosureTimeLabels() {
        List<LocalDateTime> closureTimes = dailyClosureService.getClosureTimes(startDate, endDate);
        startDateValue.setText(closureTimes.get(0).format(DATE_TIME_FORMATTER));
        endDateValue.setText(closureTimes.get(1).format(DATE_TIME_FORMATTER));
    }

    private void initPaymentMethodCombo() {
        ObservableList<PaymentMethod> paymentMethods = FXCollections.observableArrayList();
        paymentMethods.addAll(PaymentMethod.values());
        paymentMethodCombo.setItems(paymentMethods);
        paymentMethodCombo.setConverter(new PaymentMethodStringConverter(paymentMethods));
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        WaiterApp.showView(RestaurantFxmlView.class);
    }

    @Override
    public void enter() {
        updateClosureTimeLabels();
        dailyConsumptionModel = dailyConsumptionService.getDailyConsumptionModel(startDate, endDate);
        cashTotalPrice.setText(String.valueOf(dailyConsumptionModel.getConsumptionCash()));
        creditCardTotalPrice.setText(String.valueOf(dailyConsumptionModel.getConsumptionCreditCard()));
        couponTotalPrice.setText(String.valueOf(dailyConsumptionModel.getConsumptionCoupon()));
        openConsumption.setText(String.valueOf(dailyConsumptionModel.getOpenConsumption()));
        serviceFeeTotal.setText(String.valueOf(dailyConsumptionModel.getServiceFeeTotal()));
        dailySummaryTotalPrice.setText(String.valueOf(dailyConsumptionModel.getTotalConsumption()));
        productDiscount.setText(String.valueOf(dailyConsumptionModel.getProductDiscount()));
        tableDiscount.setText(String.valueOf(dailyConsumptionModel.getTableDiscount()));
        totalDiscount.setText(String.valueOf(dailyConsumptionModel.getTotalDiscount()));
        List<ReceiptRowModel> receiptRowModels = dailyConsumptionService.getReceipts(startDate, endDate);
        ObservableList<ReceiptRowModel> receiptRowList = FXCollections.observableArrayList(receiptRowModels);
        receiptTable.setItems(receiptRowList);
        receiptTable.refresh();
    }

    @FXML
    public void onPrintDailyConsumption(Event event) {
        DailyConsumptionModel dailyConsumptionModel =
                dailyConsumptionService.getDailyConsumptionModel(startDate, endDate);
        dailyConsumptionService.printAggregatedConsumption(dailyConsumptionModel);
    }

    @FXML
    public void onReloadButtonClicked(Event event) {
        try {
            startDate = LocalDate.parse(startDateTextField.getText(), DATE_FORMATTER);
            endDate = LocalDate.parse(endDateTextField.getText(), DATE_FORMATTER);
        } catch (Exception e) {
            NotificationMessage.showErrorMessage(root,
                    WaiterResources.WAITER.getString("DailySummary.InvalidDateFormat"));
            return;
        }
        enter();
    }

    @FXML
    public void onUpdatePaymentMethodButtonClicked(Event event) {
        ReceiptRowModel receiptRowModel = receiptTable.getSelectionModel().getSelectedItem();
        if (receiptRowModel == null) {
            NotificationMessage.showErrorMessage(root,
                    WaiterResources.WAITER.getString("DailySummary.NoReceiptSelected"));
            return;
        }
        PaymentMethod newPaymentMethod = paymentMethodCombo.getSelectionModel().getSelectedItem();
        if (newPaymentMethod == null) {
            NotificationMessage.showErrorMessage(root,
                    WaiterResources.WAITER.getString("DailySummary.NoPaymentMethodSelected"));
            return;
        }
        if (newPaymentMethod.toI18nString().equals(receiptRowModel.getReceiptPaymentMethod())) {
            return;
        }
        dailyConsumptionService.updatePaymentMethod(Integer.parseInt(receiptRowModel.getReceiptId()), newPaymentMethod);
        enter();
    }

    @FXML
    public void onCloseDayButtonClicked(Event event) {
        logger.info("The Daily Closure was pressed in the RestaurantView.");
        showConfirmDialog(getCloseDayConfirmString(), this::onCloseDayConfirmed);
    }

    private String getCloseDayConfirmString() {
        LocalDateTime latestClosureTime = dailyClosureService.getLatestClosureTime();
        return WaiterResources.WAITER.getString("DailyClosure.CloseDayWarning") +
                latestClosureTime.format(DATE_TIME_FORMATTER) + "\n" +
                WaiterResources.WAITER.getString("DailyClosure.CloseDayConfirm");
    }

    private void onCloseDayConfirmed() {
        try {
            int totalCommerceValue = Integer.parseInt(totalCommerce.getText());
            int otherIncomeValue = Integer.parseInt(otherIncome.getText());
            int creditCardTerminalValue = Integer.parseInt(creditCardTerminal.getText());
            int serviceFeeOverValue = Integer.parseInt(serviceFeeExtra.getText());

            dailyClosureService.closeDay();
            CloseDayParams closeDayParams = CloseDayParams.builder()
                    .totalCommerce(totalCommerceValue)
                    .otherIncome(otherIncomeValue)
                    .creditCardTerminal(creditCardTerminalValue)
                    .serviceFeeOver(serviceFeeOverValue)
                    .startDate(getDate())
                    .endDate(getDate())
                    .build();
            creditCardTerminalOver.setText(calculateCreditCardOver(creditCardTerminalValue));
            dailyConsumptionService.closeDay(closeDayParams);
            enter();
        } catch (NumberFormatException e) {
            NotificationMessage.showErrorMessage(root,
                    WaiterResources.WAITER.getString("DailySummary.InvalidNumberFormat"));
            return;
        }
    }

    private String calculateCreditCardOver(int creditCardTerminalValue) {
        // TODO: divide with 1.27
        int creditCardTerminalOver = creditCardTerminalValue - dailyConsumptionModel.getConsumptionCreditCard() -
                dailyConsumptionModel.getServiceFeeCreditCard();
        return String.valueOf((int)((double)creditCardTerminalOver / 1.27D));
    }

    private LocalDate getDate() {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.getHour() < 4) {
            return LocalDate.now().minusDays(1);
        }
        return LocalDate.now();
    }

    public class PaymentMethodStringConverter extends StringConverter<PaymentMethod> {
        private ObservableList<PaymentMethod> paymentMethods;

        public PaymentMethodStringConverter(ObservableList<PaymentMethod> paymentMethods) {
            this.paymentMethods = paymentMethods;
        }

        @Override
        public String toString(PaymentMethod productStatus) {
            return productStatus.toI18nString();
        }

        @Override
        public PaymentMethod fromString(String string) {
            return paymentMethods.stream().filter(paymentMethod -> paymentMethod.toI18nString().equals(string))
                    .collect(toList()).get(0);
        }
    }
}
