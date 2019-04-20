package com.inspirationlogical.receipt.manager.controller.receipt;

import static java.time.LocalDate.now;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsFxmlView;
import com.inspirationlogical.receipt.manager.utility.*;
import com.inspirationlogical.receipt.manager.viewmodel.*;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
public class ReceiptControllerImpl extends AbstractController implements ReceiptController {

    @FXML
    private BorderPane root;
    @FXML
    private TreeTableView<ReceiptViewModel> receiptsTable;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> date;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> type;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> status;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> paymentMethod;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> openTime;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> closureTime;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> userCode;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> sumPurchaseNetPrice;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> sumPurchaseGrossPrice;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> sumSaleNetPrice;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> sumSaleGrossPrice;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> discountPercent;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> clientName;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> clientAddress;
    @FXML
    private TreeTableColumn<ReceiptViewModel, String> clientTAXNumber;

    @FXML
    private TableView<ReceiptRecordViewModel> receiptRecordsTable;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordName;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordSoldQuantity;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordPurchasePrice;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordSalePrice;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordTotalPrice;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordVAT;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordDiscountPercent;

    private ObservableList<ReceiptRecordViewModel> receiptRecordViewModels;

    @FXML
    private Button showGoods;

    @FXML
    private Button selectiveCancellation;
    @FXML
    private TextField decreaseQuantity;

    @FXML
    private Button refreshButton;

    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;

    @FXML
    private Label numberOfReceipts;
    @FXML
    private Label cashNet;
    @FXML
    private Label cashGross;
    @FXML
    private Label creditCardNet;
    @FXML
    private Label creditCardGross;
    @FXML
    private Label couponNet;
    @FXML
    private Label couponGross;
    @FXML
    private Label totalNet;
    @FXML
    private Label totalGross;
    @FXML
    private Label totalGrossExpenditure;

    @Autowired
    private GoodsController goodsController;

    @Autowired
    private ManagerService managerService;

    private Map<LocalDate, List<ReceiptView>> receiptsByDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDatePickers();
        initReceiptController();
    }

    private void initDatePickers() {
        startDate.setValue(now().withDayOfMonth(1));
        endDate.setValue(now().withDayOfMonth(now().lengthOfMonth()));
    }

    private void initReceiptController() {
        initReceipts();
        initReceiptsColumns();
        initReceiptRecordsColumns();
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    private void initReceipts() {
        Map<LocalDate, List<ReceiptView>> unsortedReceiptsByDate = managerService.getReceipts(startDate.getValue(), endDate.getValue())
                .stream()
                .sorted(comparing(ReceiptView::getOpenTime))
                .collect(groupingBy(receiptView -> receiptView.getOpenTime().toLocalDate()));
        receiptsByDate = unsortedReceiptsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue,  newValue) -> oldValue, LinkedHashMap::new));
        SummaryCalculator.getInstance(this, receiptsByDate).calculateSummaries();
    }

    private void initReceiptsColumns() {
        initColumn(date, ReceiptViewModel::getDate);
        initColumn(type, ReceiptViewModel::getType);
        initColumn(status, ReceiptViewModel::getStatus);
        initColumn(paymentMethod, ReceiptViewModel::getPaymentMethod);
        initColumn(openTime, ReceiptViewModel::getOpenTime);
        initColumn(closureTime, ReceiptViewModel::getClosureTime);
        initColumn(sumSaleGrossPrice, ReceiptViewModel::getSumSaleGrossPrice);
        initColumn(sumSaleNetPrice, ReceiptViewModel::getSumSaleNetPrice);
        initColumn(sumPurchaseGrossPrice, ReceiptViewModel::getSumPurchaseGrossPrice);
        initColumn(sumPurchaseNetPrice, ReceiptViewModel::getSumPurchaseNetPrice);
        initColumn(discountPercent, ReceiptViewModel::getDiscountPercent);
        initColumn(userCode, ReceiptViewModel::getUserCode);
        initColumn(clientName, ReceiptViewModel::getClientName);
        initColumn(clientAddress, ReceiptViewModel::getClientAddress);
        initColumn(clientTAXNumber, ReceiptViewModel::getClientTAXNumber);

        TreeItem<ReceiptViewModel> rootItem = new TreeItem<>(new ReceiptViewModel());
        receiptsTable.setRoot(rootItem);
        receiptsTable.setShowRoot(false);
        setReceiptRowFactory();
        populateReceiptRows(rootItem);
    }

    private void initReceiptRecordsColumns() {
        initColumn(recordName, ReceiptRecordViewModel::getName);
        initColumn(recordSoldQuantity, ReceiptRecordViewModel::getSoldQuantity);
        initColumn(recordSalePrice, ReceiptRecordViewModel::getSalePrice);
        initColumn(recordTotalPrice, ReceiptRecordViewModel::getTotalPrice);
        initColumn(recordPurchasePrice, ReceiptRecordViewModel::getPurchasePrice);
        initColumn(recordVAT, ReceiptRecordViewModel::getVAT);
        initColumn(recordDiscountPercent, ReceiptRecordViewModel::getDiscountPercent);
    }

    private void setReceiptRowFactory() {
        receiptsTable.setRowFactory(tv -> {
            TreeTableRow<ReceiptViewModel> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 1 && (!row.isEmpty())) {
                    addReceiptRecordsToTable(row.getItem());
                }
            });
            return row;
        });
    }

    private void addReceiptRecordsToTable(ReceiptViewModel receipt) {
        List<ReceiptRecordViewModel> records = receipt.getRecords()
                .stream()
                .map(ReceiptRecordViewModel::new)
                .collect(toList());
        receiptRecordViewModels = observableArrayList(records);
        receiptRecordsTable.setItems(receiptRecordViewModels);
    }

    private void populateReceiptRows(TreeItem<ReceiptViewModel> rootItem) {
        receiptsByDate.forEach((date, receiptsOfDate) -> {
            TreeItem<ReceiptViewModel> dateItem = new TreeItem<>(ReceiptViewModel.getSumReceiptViewModel(date, receiptsOfDate));
            dateItem.setExpanded(false);
            rootItem.getChildren().add(dateItem);
            addRows(receiptsOfDate, dateItem);
        });
    }

    private void addRows(List<ReceiptView> receiptsOfDate, TreeItem<ReceiptViewModel> dateItem) {
        receiptsOfDate.forEach(receipt -> {
            TreeItem<ReceiptViewModel> receiptItem = new TreeItem<>(new ReceiptViewModel(receipt));
            dateItem.getChildren().add(receiptItem);
        });
    }

    @FXML
    public void onShowGoods(Event event) {
        ManagerApp.showView(GoodsFxmlView.class);
    }

    @FXML
    public void onSelectiveCancellation(Event event) {
        ReceiptRecordViewModel selectedRecordModel = receiptRecordsTable.getSelectionModel().getSelectedItem();
        ReceiptViewModel selectedReceiptModel = receiptsTable.getSelectionModel().getSelectedItem().getValue();

        if(selectedRecordModel == null || selectedReceiptModel == null) {
            ErrorMessage.showErrorMessage(getRootNode(), ManagerResources.MANAGER.getString("Receipt.SelectRecordForCancellation"));
            return;
        }
        ReceiptRecordView selectedRecordView = selectedRecordModel.getReceiptRecordView();
        if(!selectedRecordView.getOwnerStatus().equals(ReceiptStatus.CLOSED)) {
            ErrorMessage.showErrorMessage(getRootNode(), ManagerResources.MANAGER.getString("Receipt.CancellationForOpenReceipt"));
            return;
        }
        double decreaseQuantityValue = getDecreaseQuantity();
        ReceiptRecordView updatedRecordView = managerService.decreaseReceiptRecord(selectedRecordView, decreaseQuantityValue);
        receiptRecordViewModels.remove(selectedRecordModel);
        selectedReceiptModel.getRecords().remove(selectedRecordView);
        if(updatedRecordView != null) {
            selectedReceiptModel.getRecords().add(updatedRecordView);
            ReceiptRecordViewModel updatedRecordModel = new ReceiptRecordViewModel(updatedRecordView);
            receiptRecordViewModels.add(updatedRecordModel);
            receiptRecordsTable.setItems(receiptRecordViewModels);
            receiptRecordsTable.getSelectionModel().select(updatedRecordModel);
        }
    }

    private double getDecreaseQuantity() {
        String decreaseQuantityText = decreaseQuantity.getText();
        if(decreaseQuantityText.isEmpty()) {
            return 1D;
        }
        try {
            return Double.parseDouble(decreaseQuantityText);
        } catch (NumberFormatException e) {
            return 1D;
        }
    }

    @FXML
    public void onRefresh(Event event) {
        initReceiptController();
    }

    @Override
    public void setSummaryValues(SummaryValues values) {
        numberOfReceipts.setText(String.valueOf(values.getNumberOfReceipts()));
        cashNet.setText(String.valueOf(values.getCashNetIncome()));
        cashGross.setText(String.valueOf(values.getCashGrossIncome()));
        creditCardNet.setText(String.valueOf(values.getCreditCardNetIncome()));
        creditCardGross.setText(String.valueOf(values.getCreditCardGrossIncome()));
        couponNet.setText(String.valueOf(values.getCouponNetIncome()));
        couponGross.setText(String.valueOf(values.getCouponGrossIncome()));
        totalNet.setText(String.valueOf(values.getTotalNetIncome()));
        totalGross.setText(String.valueOf(values.getTotalGrossIncome()));

        totalGrossExpenditure.setText(String.valueOf(values.getTotalGrossExpenditure()));
    }
}
