package com.inspirationlogical.receipt.waiter.controller;


import java.net.URL;
import java.util.*;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewState;

import com.inspirationlogical.receipt.corelib.service.RestaurantServices;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.waiter.controller.PaymentControllerImpl.PAYMENT_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.SaleElementControllerImpl.SALE_VIEW_ELEMENT_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;

/**
 * Created by Bálint on 2017.03.22..
 */
@Singleton
public class SaleControllerImpl extends AbstractRetailControllerImpl
        implements SaleController {

    public static final String SALE_VIEW_PATH = "/view/fxml/SaleView.fxml";

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
    private Button backToRestaurantView;

    private SaleViewState saleViewState;

    private ProductCategoryView rootCategory;

    private ProductCategoryView selectedCategory;

    private List<ProductCategoryView> selectedLevelCategories;

    private List<ProductCategoryView> selectedChildrenCategories;

    private List<ProductView> visibleProducts;

    private List<SaleElementController> elementControllers;

    @Inject
    public SaleControllerImpl(RetailServices retailServices,
                              RestaurantServices restaurantServices,
                              RestaurantController restaurantController) {
        super(restaurantServices, retailServices, restaurantController);
        this.elementControllers = new ArrayList<>();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCategories();
        initializeSaleViewState();
        updateNode();
        initializeTableSummary();
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void sellProduct(ProductView productView) {
        retailServices.sellProduct(tableView, productView, 1, saleViewState.isTakeAway());
        updateSoldProductsTable();
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

    @FXML
    public void onToPaymentView(Event event) {
        PaymentController paymentController = getInjector().getInstance(PaymentController.class);
        paymentController.setTableView(tableView);
        Parent root = (Parent) loadView(PAYMENT_VIEW_PATH, paymentController);
        Main.getWindow().getScene().setRoot(root);
    }

    @FXML
    public void onTakeAwayToggled(Event event) {
        saleViewState.setTakeAway(takeAway.isSelected());
    }

    private void initializeCategories() {
        rootCategory = restaurantServices.getRootProductCategory();
        selectedCategory = rootCategory.getChildrenCategories().get(0);
    }

    private void initializeSaleViewState() {
        saleViewState = new SaleViewState();
        saleViewState.setFullScreen(restaurantController.getViewState().isFullScreen());
    }

    private void updateNode() {
        updateSoldProductsTable();
        updateCategories(selectedCategory);
    }

    private void updateCategories(ProductCategoryView selectedCategory) {
        if(!ProductCategoryType.isLeaf(selectedCategory.getType())) {
            selectedLevelCategories = selectedCategory.getParent().getChildrenCategories();
            selectedChildrenCategories = selectedCategory.getChildrenCategories();
        }
        visibleProducts = selectedCategory.getAllProducts();
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
//        SaleElementController<T> elementController =
//                getInjector().getInstance(SaleElementControllerImpl.class);
//                getInjector().getInstance(SaleElementController.class);

        SaleElementController elementController = null;

        if(elementView instanceof ProductView) {
            elementController = new SaleProductControllerImpl(this);
        } else if (elementView instanceof ProductCategoryView) {
            elementController = new SaleCategoryControllerImpl(this);
        }
        elementController.setView(elementView);
        elementControllers.add(elementController);
        Node root = loadView(SALE_VIEW_ELEMENT_PATH, elementController);
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
        elementController.setView(new ProductView() {
            @Override
            public String getName() {
                return "Vissza";
            }
        });
        loadView(SALE_VIEW_ELEMENT_PATH, elementController);
        categoriesGrid.add(elementController.getRootNode(), 3, 3);
    }
}
