package com.inspirationlogical.receipt.waiter.controller.reatail.sale;


import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleViewState.CancellationType.NONE;
import static com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleViewState.CancellationType.SELECTIVE;
import static com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleViewState.CancellationType.SINGLE;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;

/**
 * Created by Bálint on 2017.03.22..
 */
@Singleton
public class SaleControllerImpl extends AbstractRetailControllerImpl
        implements SaleController {

    public static final String SALE_VIEW_PATH = "/view/fxml/Sale.fxml";

    private final int GRID_SIZE = 4;

    @FXML
    private BorderPane root;

    @FXML
    private GridPane categoriesGrid;

    @FXML
    private GridPane subCategoriesGrid;

    @FXML
    private GridPane productsGrid;

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
    private TextField searchField;

    @FXML
    private Button backToRestaurantView;

    @FXML
    private Label liveTime;

    @Inject
    private ViewLoader viewLoader;

    private Popup adHocProductForm;

    private CommonService commonService;

    private AdHocProductFormController adHocProductFormController;

    private SaleViewState saleViewState;

    private Search productSearcher;

    private List<ProductView> searchedProducts;

    private List<SaleElementController> elementControllers;

    private VisibleProductControllerImpl productController;

    @Inject
    public SaleControllerImpl(RetailService retailService,
                              RestaurantService restaurantService,
                              CommonService commonService,
                              RestaurantController restaurantController,
                              AdHocProductFormController adHocProductFormController) {
        super(restaurantService, retailService, restaurantController);
        this.commonService = commonService;
        this.adHocProductFormController = adHocProductFormController;
        saleViewState = new SaleViewState();
        productController = new VisibleProductControllerImpl(commonService);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSoldProductsTable();
        initializeToggles();
        updateNode();
        initializeSoldProductsTableRowHandler();
        initializeQuickSearchAndSellHandler();
        initLiveTime(liveTime);
        initializeProductSearcher();
    }

    @Override
    public String getViewPath() {
        return SALE_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void sellProduct(ProductView productView) {
        retailService.sellProduct(tableView, productView, 1, saleViewState.isTakeAway(), saleViewState.isGift());
        getSoldProductsAndUpdateTable();
    }

    @Override
    public void sellAdHocProduct(AdHocProductParams adHocProductParams) {
        retailService.sellAdHocProduct(tableView, adHocProductParams, saleViewState.isTakeAway());
        getSoldProductsAndUpdateTable();
        adHocProductForm.hide();
    }

    @Override
    public void selectCategory(SaleElementController saleElementController) {
        productController.selectCategory(saleElementController);
        redrawCategories();
    }

    @Override
    public void upWithCategories() {
        productController.upWithCategories();
        redrawCategories();
    }

    @Override
    public void updateNode() {
        if(!soldProductsTableInitialized) {
            return;
        }
        getSoldProductsAndUpdateTable();
        productController.updateCategories();
        redrawCategories();
        updateTableSummary();
        resetToggleGroups();
    }

    @Override
    public void clearSearch() {
        searchField.clear();
    }

    @Override
    protected void soldProductsRowClickHandler(SoldProductViewModel row) {
        if(saleViewState.isSelectiveCancellation()) {
            retailService.cancelReceiptRecord(tableView, removeRowFromSoldProducts(row));
            getSoldProductsAndUpdateTable();
        } else if(saleViewState.isSingleCancellation()) {
            decreaseRowInSoldProducts(row, 1);
            getSoldProductsAndUpdateTable();
        } else {
            increaseRowInSoldProducts(row, 1, true);
            getSoldProductsAndUpdateTable();
        }
    }

    @FXML
    public void onToPaymentView(Event event) {
        retailService.mergeReceiptRecords(receiptView);
        PaymentController paymentController = WaiterRegistry.getInstance(PaymentController.class);
        paymentController.setTableView(tableView);
        paymentController.updateNode();
        viewLoader.loadViewIntoScene(paymentController);
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
        showPopup(adHocProductForm, adHocProductFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onGiftProduct(Event event) {
        setGiftProduct();
    }

    @FXML
    public void onSearchChanged(Event event) {
        String searchText = searchField.getText();
        productsGrid.getChildren().clear();
        if (!searchText.isEmpty()) {
            searchedProducts = productSearcher.search(searchText);
            drawListOfElements(searchedProducts, productsGrid);
        } else {
            drawListOfElements(productController.getVisibleProducts(), productsGrid);
        }
    }

    private void initializeProductSearcher() {
        productSearcher = new SearchProduct(commonService.getSellableProducts());
    }

    private void initializeToggles() {
        singleCancellation.setUserData(SINGLE);
        selectiveCancellation.setUserData(SELECTIVE);
        saleViewState.setCancellationType(NONE);
        cancellationTypeToggleGroup.selectedToggleProperty().addListener(new CancellationTypeToggleListener());
        sortByClickTime.selectedProperty().addListener(new SortByClickTimeToggleListener());
    }

    private void initializeQuickSearchAndSellHandler() {
        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    if (searchedProducts.size() == 1) {
                        sellProduct(searchedProducts.get(0));
                    }
                    break;
                case DELETE:
                    searchField.clear();
                    productController.updateCategories();
                    redrawCategories();
                    break;
                case BACK_SPACE:
                    if (searchFieldNotEmpty()) {
                        searchField.setText(searchField.getText(0, searchField.getText().length() - 1));
                    }
                default:
                    searchField.appendText(keyEvent.getText());
                    onSearchChanged(keyEvent);
                    break;
            }
        });
    }

    private boolean searchFieldNotEmpty() {
        return !searchField.getText().isEmpty();
    }

    private void redrawCategories() {
        categoriesGrid.getChildren().clear();
        subCategoriesGrid.getChildren().clear();
        productsGrid.getChildren().clear();
        elementControllers = new ArrayList<>();
        drawListOfElements(productController.getSelectedLevelCategories(), categoriesGrid);
        drawBackButton(categoriesGrid);
        drawListOfElements(productController.getSelectedChildrenCategories(), subCategoriesGrid);
        drawListOfElements(productController.getVisibleProducts(), productsGrid);
        setSelectedElement(true);
    }

    private void setSelectedElement(boolean selected) {
        elementControllers.stream()
                .filter(controller -> controller.getView().getName().equals(productController.getSelectedCategory().getName()))
                .forEach(controller -> controller.select(selected));
    }

    private <T extends AbstractView> void drawListOfElements(List<T> elements, GridPane grid) {
        elements.sort(Comparator.comparing(AbstractView::getOrderNumber));
        for(int i = 0; i < Math.min(elements.size(), 32); i++) {
            drawElement(elements.get(i), grid, i);
        }
    }

    private <T extends AbstractView> void drawElement(T elementView, GridPane grid, int index) {
        SaleElementController elementController = null;

        if(elementView instanceof ProductView) {
            elementController = new SaleProductControllerImpl(this);
        } else if (elementView instanceof ProductCategoryView) {
            elementController = new SaleCategoryControllerImpl(this);
        }
        elementController.setView(elementView);
        elementControllers.add(elementController);
        viewLoader.loadView(elementController);
        grid.add(elementController.getRootNode(), index % GRID_SIZE, index / GRID_SIZE);
    }

    private void drawBackButton(GridPane categoriesGrid) {
        final int BUTTON_POSITION = GRID_SIZE - 1;
        SaleElementController elementController = null;

        elementController = new SaleProductControllerImpl(this) {

            @Override
            public void onElementClicked(MouseEvent event) {
                productController.upWithCategories();
                redrawCategories();
            }
        };
        elementController.setView(new ProductCategoryView() {
            @Override
            public String getCategoryName() {
                return null;
            }

            @Override
            public ProductCategoryView getParent() {
                return null;
            }

            @Override
            public ProductView getProduct() {
                return null;
            }

            @Override
            public ProductCategoryType getType() {
                return null;
            }

            @Override
            public ProductStatus getStatus() {
                return null;
            }

            @Override
            public String getName() {
                return "Vissza";
            }
        });
        viewLoader.loadView(elementController);
        categoriesGrid.add(elementController.getRootNode(), BUTTON_POSITION, BUTTON_POSITION);
    }

    private void setGiftProduct() {
        saleViewState.setGift(giftProduct.isSelected());
    }

    private void resetToggleGroups() {
        cancellationTypeToggleGroup.selectToggle(null);
        giftProduct.setSelected(false);
    }

    private class CancellationTypeToggleListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if(cancellationTypeToggleGroup.getSelectedToggle() == null) {
                saleViewState.setCancellationType(NONE);
                return;
            }
            saleViewState.setCancellationType((SaleViewState.CancellationType)cancellationTypeToggleGroup.getSelectedToggle().getUserData());
        }
    }

    private class SortByClickTimeToggleListener implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue) {
                sortSoldProductByLatestClickTime();
            } else {
                getSoldProductsAndUpdateTable();
            }
        }
    }
}
