package com.inspirationlogical.receipt.manager.controller;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.viewmodel.ReceiptRecordViewModel;
import com.inspirationlogical.receipt.manager.viewmodel.ReceiptViewModel;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

@Singleton
public class ReceiptControllerImpl extends AbstractController implements ReceiptController {

    public static final String RECEIPT_VIEW_PATH = "/view/fxml/Receipt.fxml";

    @FXML
    private BorderPane root;
    @FXML
    TreeTableView<ReceiptViewModel> receiptsTable;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> date;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> type;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> status;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> paymentMethod;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> openTime;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> closureTime;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> userCode;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> sumPurchaseNetPrice;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> sumPurchaseGrossPrice;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> sumSaleNetPrice;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> sumSaleGrossPrice;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> discountPercent;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> VATSerie;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> clientName;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> clientAddress;
    @FXML
    TreeTableColumn<ReceiptViewModel, String> clientTAXNumber;

    @FXML
    TableView<ReceiptRecordViewModel> receiptRecordsTable;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordName;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordType;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordSoldQuantity;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordAbsoluteQuantity;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordPurchasePrice;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordSalePrice;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordVAT;
    @FXML
    TableColumn<ReceiptRecordViewModel, String> recordDiscountPercent;

    @FXML
    Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    private GoodsController goodsController;

    private CommonService commonService;

    private Map<LocalDate, List<ReceiptView>> receiptsByDate;

    @Inject
    public ReceiptControllerImpl(GoodsController goodsController, CommonService commonService) {
        this.goodsController = goodsController;
        this.commonService = commonService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initReceipts();
        initReceiptsColumns();
        initReceiptRecordsColumns();
    }

    @Override
    public String getViewPath() {
        return RECEIPT_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @FXML
    public void onShowGoods(Event event) {
        viewLoader.loadViewIntoScene(goodsController);
    }

    private void initReceipts() {
        receiptsByDate = commonService.getReceipts()
                .stream()
                .sorted(comparing(ReceiptView::getOpenTime))
                .collect(groupingBy(receiptView -> receiptView.getOpenTime().toLocalDate()));
    }

    private void initReceiptsColumns() {
        initColumn(date, ReceiptViewModel::getDate);
        initColumn(type, ReceiptViewModel::getType);
        initColumn(status, ReceiptViewModel::getStatus);
        initColumn(paymentMethod, ReceiptViewModel::getPaymentMethod);
        initColumn(openTime, ReceiptViewModel::getOpenTime);
        initColumn(closureTime, ReceiptViewModel::getClosureTime);
        initColumn(userCode, ReceiptViewModel::getUserCode);
        initColumn(sumPurchaseNetPrice, ReceiptViewModel::getSumPurchaseNetPrice);
        initColumn(sumPurchaseGrossPrice, ReceiptViewModel::getSumPurchaseGrossPrice);
        initColumn(sumSaleNetPrice, ReceiptViewModel::getSumSaleNetPrice);
        initColumn(sumSaleGrossPrice, ReceiptViewModel::getSumSaleGrossPrice);
        initColumn(discountPercent, ReceiptViewModel::getDiscountPercent);
        initColumn(VATSerie, ReceiptViewModel::getVATSerie);
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
        initColumn(recordType, ReceiptRecordViewModel::getType);
        initColumn(recordSoldQuantity, ReceiptRecordViewModel::getSoldQuantity);
        initColumn(recordAbsoluteQuantity, ReceiptRecordViewModel::getAbsoluteQuantity);
        initColumn(recordPurchasePrice, ReceiptRecordViewModel::getPurchasePrice);
        initColumn(recordSalePrice, ReceiptRecordViewModel::getSalePrice);
        initColumn(recordVAT, ReceiptRecordViewModel::getVAT);
        initColumn(recordDiscountPercent, ReceiptRecordViewModel::getDiscountPercent);
    }

    private void setReceiptRowFactory() {
        receiptsTable.setRowFactory(tv -> {
            TreeTableRow<ReceiptViewModel> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 1 && (! row.isEmpty())) {
                    List<ReceiptRecordViewModel> records = row.getItem().getRecords()
                            .stream()
                            .map(ReceiptRecordViewModel::new)
                            .collect(toList());
                    receiptRecordsTable.setItems(observableArrayList(records));
                    row.getItem();
                }
            });
            return row;
        });
    }

    private void populateReceiptRows(TreeItem<ReceiptViewModel> rootItem) {
        receiptsByDate.forEach((date, receiptsOfDate) -> {
            TreeItem<ReceiptViewModel> dateItem = new TreeItem<>(new ReceiptViewModel(date));
            dateItem.setExpanded(true);
            rootItem.getChildren().add(dateItem);
            addPaymentMethodSumRows(receiptsOfDate, dateItem);
            addRows(receiptsOfDate, dateItem);
        });
    }

    private void addPaymentMethodSumRows(List<ReceiptView> receiptsOfDate, TreeItem<ReceiptViewModel> dateItem) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            List<ReceiptView> receipts = receiptsOfDate
                    .stream()
                    .filter(receiptView -> receiptView.getPaymentMethod().equals(paymentMethod))
                    .collect(toList());
            if (!receipts.isEmpty()) {
                TreeItem<ReceiptViewModel> receiptItem = new TreeItem<>(new ReceiptViewModel(paymentMethod, receipts));
                dateItem.getChildren().add(receiptItem);
            }
        }
    }

    private void addRows(List<ReceiptView> receiptsOfDate, TreeItem<ReceiptViewModel> dateItem) {
        receiptsOfDate.forEach(receipt -> {
            TreeItem<ReceiptViewModel> receiptItem = new TreeItem<>(new ReceiptViewModel(receipt));
            dateItem.getChildren().add(receiptItem);
        });
    }
}
