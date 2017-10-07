package com.inspirationlogical.receipt.waiter.controller.reatail.sale;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentControllerImpl;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
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

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleViewState.CancellationType.NONE;

/**
 * Created by Bálint on 2017.03.22..
 */
@Singleton
public class SaleControllerImpl extends AbstractRetailControllerImpl
        implements SaleController {

    final private static Logger logger = LoggerFactory.getLogger(PaymentControllerImpl.class);

    private static final String SALE_VIEW_PATH = "/view/fxml/Sale.fxml";

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
    ToggleButton sortByClickTime;

    @FXML
    TextField searchField;

    @FXML
    private Button backToRestaurantView;

    @FXML
    Label liveTime;

    private Popup adHocProductForm;

    private @Inject CommonService commonService;

    private @Inject PaymentController paymentController;
    private @Inject AdHocProductFormController adHocProductFormController;
    @Inject ProductsAndCategoriesController productController;

    SaleViewState saleViewState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saleViewState = new SaleViewState();
        initializeSoldProducts();
        new SaleControllerInitializer(this).initialize();
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
    public String getViewPath() {
        return SALE_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return rootSale;
    }

    @Override
    public void sellProduct(ProductView productView) {
        retailService.sellProduct(tableView, productView, 1, saleViewState.isTakeAway(), saleViewState.isGift());
        getSoldProductsAndRefreshTable();
    }

    @Override
    public void sellAdHocProduct(AdHocProductParams adHocProductParams) {
        retailService.sellAdHocProduct(tableView, adHocProductParams, saleViewState.isTakeAway());
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
    protected void soldProductsRowClickHandler(SoldProductViewModel row) {
        logger.info("The sold products table was clicked on the row: " + row.toString() + "in sale view state: " + saleViewState.toString());
        disableSoldProductsTableRowClickHandler();
        if(saleViewState.isSelectiveCancellation()) {
            retailService.cancelReceiptRecord(tableView, removeRowFromSoldProducts(row));
            getSoldProductsAndRefreshTable();
        } else if(saleViewState.isSingleCancellation()) {
            decreaseRowInSoldProducts(row, 1);
            getSoldProductsAndRefreshTable();
        } else {
            increaseRowInSoldProducts(row, 1, true);
            getSoldProductsAndRefreshTable();
        }
        enableSoldProductsTableRowClickHandler();
    }

    @FXML
    public void onToPaymentView(Event event) {
        logger.info("Entering the payment view for table: " + tableView.toString());
        retailService.mergeReceiptRecords(receiptView);
        paymentController.setTableView(tableView);
        viewLoader.loadViewIntoScene(paymentController);
        paymentController.enterPaymentView();
    }

    @FXML
    public void onSellAdHocProduct(Event event) {
        logger.info("The sell ad hoc product button was clicked.");
        adHocProductForm = new Popup();
        adHocProductForm.getContent().add(viewLoader.loadView(adHocProductFormController));
        adHocProductFormController.loadAdHocProductForm(this);
        showPopup(adHocProductForm, adHocProductFormController, rootSale, new Point2D(520, 200));
    }

    @FXML
    public void onSearchChanged(Event event) {
        productController.search(searchField.getText());
    }

    private void resetToggleGroups() {
        cancellationTypeToggleGroup.selectToggle(null);
        giftProduct.setSelected(false);
        sortByClickTime.setSelected(false);
        sortByClickTime.requestFocus();
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

    ChangeListener<Boolean> sortByClickTimeToggleListener = (observable, oldValue, newValue) -> {
        if(newValue) {
            sortSoldProductByLatestClickTime();
        } else {
            getSoldProductsAndRefreshTable();
        }
    };
}
