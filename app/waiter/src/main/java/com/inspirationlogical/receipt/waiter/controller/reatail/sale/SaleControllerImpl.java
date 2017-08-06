package com.inspirationlogical.receipt.waiter.controller.reatail.sale;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
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

    public static final String SALE_VIEW_PATH = "/view/fxml/Sale.fxml";

    @FXML
    BorderPane rootSale;

    @FXML
    GridPane categoriesGrid;

    @FXML
    GridPane subCategoriesGrid;

    @FXML
    GridPane productsGrid;

    @FXML
    private RadioButton takeAway;

    @FXML
    private Button sellAdHocProduct;

    @FXML
    private ToggleButton giftProduct;

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

    private CommonService commonService;

    private AdHocProductFormController adHocProductFormController;

    SaleViewState saleViewState;

    ProductsAndCategoriesController productController;

    @Inject
    public SaleControllerImpl(RetailService retailService,
                              RestaurantService restaurantService,
                              CommonService commonService,
                              RestaurantController restaurantController,
                              AdHocProductFormController adHocProductFormController,
                              ProductsAndCategoriesController productController,
                              TableConfigurationController tableConfigurationController) {
        super(restaurantService, retailService, restaurantController, tableConfigurationController );
        this.commonService = commonService;
        this.adHocProductFormController = adHocProductFormController;
        this.productController = productController;
        saleViewState = new SaleViewState();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSoldProducts();
        new SaleControllerInitializer(this).initialize();
    }

    @Override
    public void enterSaleView() {
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
    }

    @FXML
    public void onToPaymentView(Event event) {
        retailService.mergeReceiptRecords(receiptView);
        PaymentController paymentController = WaiterRegistry.getInstance(PaymentController.class);
        paymentController.setTableView(tableView);
        viewLoader.loadViewIntoScene(paymentController);
        paymentController.enterPaymentView();
    }

    @FXML
    public void onTakeAwayToggled(Event event) {
        saleViewState.setTakeAway(takeAway.isSelected());
    }

    @FXML
    public void onSellAdHocProduct(Event event) {
        adHocProductForm = new Popup();
        adHocProductForm.getContent().add(viewLoader.loadView(adHocProductFormController));
        adHocProductFormController.loadAdHocProductForm(this);
        showPopup(adHocProductForm, adHocProductFormController, rootSale, new Point2D(520, 200));
    }

    @FXML
    public void onGiftProduct(Event event) {
        setGiftProduct();
    }

    @FXML
    public void onSearchChanged(Event event) {
        productController.search(searchField.getText());
    }

    private void setGiftProduct() {
        saleViewState.setGift(giftProduct.isSelected());
    }

    private void resetToggleGroups() {
        cancellationTypeToggleGroup.selectToggle(null);
        giftProduct.setSelected(false);
        sortByClickTime.setSelected(false);
        sortByClickTime.requestFocus();
    }

    ChangeListener<Toggle> cancellationTypeToggleListener = (observable, oldValue, newValue) -> {
        if(cancellationTypeToggleGroup.getSelectedToggle() == null) {
            saleViewState.setCancellationType(NONE);
            return;
        }
        saleViewState.setCancellationType((SaleViewState.CancellationType)cancellationTypeToggleGroup.getSelectedToggle().getUserData());
    };

    ChangeListener<Boolean> sortByClickTimeToggleListener = (observable, oldValue, newValue) -> {
        if(newValue) {
            sortSoldProductByLatestClickTime();
        } else {
            getSoldProductsAndRefreshTable();
        }
    };
}
