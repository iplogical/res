package com.inspirationlogical.receipt.manager.controller.receipt;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;
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

    private static final String RECEIPT_VIEW_PATH = "/view/fxml/Receipt.fxml";

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
    private TreeTableColumn<ReceiptViewModel, String> VATSerie;
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
    private TableColumn<ReceiptRecordViewModel, String> recordType;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordSoldQuantity;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordAbsoluteQuantity;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordPurchasePrice;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordSalePrice;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordVAT;
    @FXML
    private TableColumn<ReceiptRecordViewModel, String> recordDiscountPercent;

    @FXML
    Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    @Inject
    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    @Inject
    private ManagerService managerService;

    private Map<LocalDate, List<ReceiptView>> receiptsByDate;

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
        Map<LocalDate, List<ReceiptView>> unsortedReceiptsByDate = managerService.getReceipts()
                .stream()
                .sorted(comparing(ReceiptView::getOpenTime))
                .collect(groupingBy(receiptView -> receiptView.getOpenTime().toLocalDate()));
        receiptsByDate = unsortedReceiptsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue,  newValue) -> oldValue, LinkedHashMap::new));
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
        initColumn(VATSerie, ReceiptViewModel::getVATSerie);
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
}
