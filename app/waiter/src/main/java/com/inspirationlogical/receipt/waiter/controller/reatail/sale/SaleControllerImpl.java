package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;

import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentFxmlView;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.buttons.ProductsAndCategoriesController;
import com.inspirationlogical.receipt.waiter.viewmodel.ProductRowModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleViewState.CancellationType.NONE;

/**
 * Created by Bálint on 2017.03.22..
 */
@FXMLController
public class SaleControllerImpl extends AbstractRetailControllerImpl
        implements SaleController {

    final private static Logger logger = LoggerFactory.getLogger(PaymentControllerImpl.class);

    @FXML
    BorderPane rootSale;

    @FXML
    GridPane categoriesGrid;

    @FXML
    GridPane subCategoriesGrid;

    @FXML
    GridPane productsGrid;

    @FXML
    RadioButton takeAway;

    @FXML
    private Button sellAdHocProduct;

    @FXML
    ToggleButton giftProduct;

    @FXML
    ToggleButton selectiveCancellation;
    @FXML
    ToggleButton singleCancellation;
    @FXML
    ToggleGroup cancellationTypeToggleGroup;

    @FXML
    TextField searchField;

    @FXML
    private Button backToRestaurantView;

    @FXML
    private Button orderDelivered;

    @FXML
    Label liveTime;

    private Popup adHocProductForm;

    @Autowired
    private PaymentController paymentController;

    @Autowired
    private AdHocProductFormController adHocProductFormController;

    @Autowired
    ProductsAndCategoriesController productController;

    SaleViewState saleViewState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saleViewState = new SaleViewState();
        initializeSoldProducts();
        new SaleControllerInitializer(this).initialize();
        initAdHocProductForm();
    }

    private void initAdHocProductForm() {
        adHocProductForm = new Popup();
        adHocProductForm.getContent().add(WaiterApp.getRootNode(AdHocProductFormFxmlView.class));
    }

    @Override
    public void enterSaleView() {
        logger.info("Entering the sale view for table: " + tableView.toString());
        getSoldProductsAndRefreshTable();
        productController.initCategoriesAndProducts();
        updateTableSummary();
        resetToggleGroups();
        clearSearch();
    }

    @Override
    public Node getRootNode() {
        return rootSale;
    }

    @Override
    public void sellProduct(ProductView productView) {
        receiptService.sellProduct(tableView, productView, 1, saleViewState.isTakeAway(), saleViewState.isGift());
        setOrderDelivered(false);
        getSoldProductsAndRefreshTable();
    }

    @Override
    public void sellAdHocProduct(AdHocProductParams adHocProductParams) {
        receiptService.sellAdHocProduct(tableView, adHocProductParams, saleViewState.isTakeAway());
        setOrderDelivered(false);
        getSoldProductsAndRefreshTable();
        adHocProductForm.hide();
    }

    @Override
    public void hideAdHocProductForm() {
        adHocProductForm.hide();
    }

    @Override
    public void clearSearch() {
        searchField.clear();
    }

    @Override
    protected void onSoldProductsRowClick(ProductRowModel row) {
        logger.info("The sold products table was clicked on the row: " + row.toString() + "in sale view state: " + saleViewState.toString());
        disableSoldProductsTableRowClickHandler();
        handleSoldProductsTableRowClick(row);
        enableSoldProductsTableRowClickHandler();
    }

    private void handleSoldProductsTableRowClick(ProductRowModel row) {
        if(saleViewState.isSelectiveCancellation()) {
            ReceiptRecordView clickedRecord = findMatchingSoldProduct(row);
            soldProductViewList.remove(clickedRecord);
            receiptRecordService.cancelReceiptRecord(clickedRecord);
        } else if(saleViewState.isSingleCancellation()) {
            ReceiptRecordView clickedRecord = findMatchingSoldProduct(row);
            receiptRecordService.decreaseSoldQuantity(clickedRecord, 1);
        } else {
            increaseRowInSoldProducts(row, 1, true);
            setOrderDelivered(false);
        }
        getSoldProductsAndRefreshTable();
    }

    @FXML
    public void onToPaymentView(Event event) {
        logger.info("Entering the payment view for table: " + tableView.toString());
        receiptService.mergeReceiptRecords(receiptView);
        paymentController.setTableView(tableView);
        WaiterApp.showView(PaymentFxmlView.class);
        paymentController.enterPaymentView();
    }

    @FXML
    public void onSellAdHocProduct(Event event) {
        logger.info("The sell ad hoc product button was clicked.");
        showPopup(adHocProductForm, adHocProductFormController, rootSale, new Point2D(520, 200));
    }

    @FXML
    public void onSearchChanged(Event event) {
        productController.search(searchField.getText());
    }

    @FXML
    public void onOrderDelivered(Event event) {
        setOrderDelivered(true);
        backToRestaurantView();
    }

    private void setOrderDelivered(boolean isDelivered) {
        getTableController().setOrderDelivered(isDelivered);
    }

    private void resetToggleGroups() {
        cancellationTypeToggleGroup.selectToggle(null);
        giftProduct.setSelected(false);
    }

    ChangeListener<Boolean> takeAwayChangeListener = (observable, oldValue, newValue) -> {
        logger.info("The take away button was clicked. New value: " + takeAway.isSelected());
        saleViewState.setTakeAway(takeAway.isSelected());

    };

    ChangeListener<Boolean> giftProductToggleListener = (observable, oldValue, newValue) -> {
        logger.info("The gift product button was clicked. New value: " + giftProduct.isSelected());
        saleViewState.setGift(giftProduct.isSelected());
    };

    ChangeListener<Toggle> cancellationTypeToggleListener = (observable, oldValue, newValue) -> {
        if(cancellationTypeToggleGroup.getSelectedToggle() == null) {
            logger.info("The cancellation type changed to NONE.");
            saleViewState.setCancellationType(NONE);
            return;
        }
        SaleViewState.CancellationType type = (SaleViewState.CancellationType)cancellationTypeToggleGroup.getSelectedToggle().getUserData();
        logger.info("The cancellation type was changed to " + type.toString());
        saleViewState.setCancellationType(type);
    };
}
