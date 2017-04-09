package com.inspirationlogical.receipt.waiter.controller;


import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewState;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
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
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCategories();
        initializeSoldProductsTable();
        initializeSaleViewState();
        updateNode();
        updateTableSummary();
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

    private void initializeCategories() {
        rootCategory = commonService.getRootProductCategory();
        selectedCategory = rootCategory.getChildrenCategories().get(0);
    }

    private void initializeSaleViewState() {
        saleViewState = new SaleViewState();
    }

    private void updateCategories(ProductCategoryView selectedCategory) {
        if(!ProductCategoryType.isLeaf(selectedCategory.getType())) {
            selectedLevelCategories = selectedCategory.getParent().getChildrenCategories();
            selectedChildrenCategories = selectedCategory.getChildrenCategories();
        }
        visibleProducts = selectedCategory.getAllNormalProducts();
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
        elementControllers.stream().filter(controller -> controller.getView().getName().equals(selectedCategory.getName()))
                .map(controller -> {controller.select(true);
                    return true;})
                .collect(Collectors.toList());
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
            public List<ProductCategoryView> getChildrenCategories() {
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
}
