package com.inspirationlogical.receipt.waiter.controller;


import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.corelib.utility.Search.MATCH_HEAD_IGNORE_CASE;
import static com.inspirationlogical.receipt.corelib.utility.Search.SPACE;
import static com.inspirationlogical.receipt.waiter.viewstate.SaleViewState.CancellationType.NONE;
import static com.inspirationlogical.receipt.waiter.viewstate.SaleViewState.CancellationType.SELECTIVE;
import static com.inspirationlogical.receipt.waiter.viewstate.SaleViewState.CancellationType.SINGLE;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import com.inspirationlogical.receipt.corelib.utility.Search.SearchBuilder;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewState;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
    private TextField search;

    @FXML
    private Button backToRestaurantView;

    @Inject
    private ViewLoader viewLoader;

    private Popup adHocProductForm;

    private CommonService commonService;

    private AdHocProductFormController adHocProductFormController;

    private SaleViewState saleViewState;

    private ProductCategoryView rootCategory;

    private ProductCategoryView selectedCategory;

    private List<ProductCategoryView> selectedLevelCategories;

    private List<ProductCategoryView> selectedChildrenCategories;

    private List<ProductView> visibleProducts;

    private List<SaleElementController> elementControllers;


    @Inject
    public SaleControllerImpl(RetailService retailService,
                              RestaurantService restaurantService,
                              CommonService commonService,
                              RestaurantController restaurantController,
                              AdHocProductFormController adHocProductFormController) {
        super(restaurantService, retailService, restaurantController);
        this.commonService = commonService;
        this.adHocProductFormController = adHocProductFormController;
        this.elementControllers = new ArrayList<>();
        saleViewState = new SaleViewState();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCategories();
        initializeSoldProductsTable();
        initializeCancellationToggles();
        updateNode();
        updateTableSummary();
        initializeSoldProductsTableRowHandler();
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
        soldProductsView = getSoldProducts(restaurantService, tableView);
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
    }

    @Override
    public void sellAdHocProduct(AdHocProductParams adHocProductParams) {
        retailService.sellAdHocProduct(tableView, adHocProductParams, saleViewState.isTakeAway());
        soldProductsView = getSoldProducts(restaurantService, tableView);
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
        adHocProductForm.hide();
    }

    @Override
    public void selectCategory(SaleElementController saleElementController) {
        selectedCategory = (ProductCategoryView) saleElementController.getView();
        updateCategories(selectedCategory);
    }

    @Override
    public void upWithCategories() {
        if(ProductCategoryType.isRoot(selectedCategory.getParent().getType())) {
            return;
        }
        selectedCategory = selectedCategory.getParent();
        updateCategories(selectedCategory);
    }

    @Override
    public void updateNode() {
        if(!soldProductsTableInitialized) {
            return;
        }
        soldProductsView = getSoldProducts(restaurantService, tableView);
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
        updateCategories(selectedCategory);
    }

    @Override
    public void clearSearch() {
        search.clear();
    }

    @Override
    protected void rowClickHandler(SoldProductViewModel row) {
        if(saleViewState.isSelectiveCancellation()) {
            retailService.cancelReceiptRecord(tableView, removeRowFromSoldProducts(row));
        } else if(saleViewState.isSingleCancellation()) {
            decreaseRowInSoldProducts(row, 1);
        }
    }

    @FXML
    public void onToPaymentView(Event event) {
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
    public void onSearchSelected(Event event) {
        setSelectedElement(false);
        visibleProducts = commonService.getSellableProducts(selectedCategory);
    }

    @FXML
    public void onSearchChanged(Event event) {
        String text = search.getText();
        List<ProductView> matches;
        try {
            int rapidCode = Integer.parseInt(text);
            matches = visibleProducts.stream()
                    .filter(productView -> productView.getRapidCode() == rapidCode)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            matches = new SearchBuilder<>(visibleProducts)
                    .withDelimiter(SPACE)
                    .withPattern(text)
                    .withFormat(MATCH_HEAD_IGNORE_CASE)
                    .search();
        }
        productsGrid.getChildren().clear();
        drawListOfElements(matches, productsGrid);
    }

    private void initializeCategories() {
        rootCategory = commonService.getRootProductCategory();
        selectedCategory = commonService.getChildCategories(rootCategory).get(0);
    }

    private void initializeCancellationToggles() {
        singleCancellation.setUserData(SINGLE);
        selectiveCancellation.setUserData(SELECTIVE);
        saleViewState.setCancellationType(NONE);
        cancellationTypeToggleGroup.selectedToggleProperty().addListener(new CancellationTypeToggleListener());
    }

    private void updateCategories(ProductCategoryView selectedCategory) {
        if(!ProductCategoryType.isLeaf(selectedCategory.getType())) {
            selectedLevelCategories = commonService.getChildCategories(selectedCategory.getParent());
            selectedChildrenCategories = commonService.getChildCategories(selectedCategory);
        }
        visibleProducts = commonService.getSellableProducts(selectedCategory);
        redrawCategories(selectedCategory);
    }

    private void redrawCategories(ProductCategoryView selectedCategory) {
        categoriesGrid.getChildren().clear();
        subCategoriesGrid.getChildren().clear();
        productsGrid.getChildren().clear();
        elementControllers = new ArrayList<>();
        drawListOfElements(selectedLevelCategories, categoriesGrid);
        drawBackButton(categoriesGrid);
        drawListOfElements(selectedChildrenCategories, subCategoriesGrid);
        drawListOfElements(visibleProducts, productsGrid);
        setSelectedElement(true);
    }

    private void setSelectedElement(boolean selected) {
        elementControllers.stream()
                .filter(controller -> controller.getView().getName().equals(selectedCategory.getName()))
                .forEach(controller -> controller.select(selected));
    }

    private <T extends AbstractView> void drawListOfElements(List<T> elements, GridPane grid) {
        elements.sort(AbstractView.compareNames);
        for(int i = 0; i < elements.size(); i++) {
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
        grid.add(elementController.getRootNode(), index % 4, index / 4);
    }

    private void drawBackButton(GridPane categoriesGrid) {
        SaleElementController elementController = null;

        elementController = new SaleProductControllerImpl(this) {

            @Override
            public void onElementClicked(MouseEvent event) {
                saleController.upWithCategories();
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
            public List<ProductCategoryView> getChildCategories() {
                return null;
            }

            @Override
            public ProductView getProduct() {
                return null;
            }

            @Override
            public List<ProductView> getAllProducts() {
                return null;
            }

            @Override
            public List<ProductView> getAllActiveProducts() {
                return null;
            }

            @Override
            public List<ProductView> getAllNormalProducts() {
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
        categoriesGrid.add(elementController.getRootNode(), 3, 3);
    }

    private void setGiftProduct() {
        saleViewState.setGift(giftProduct.isSelected());
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
}
