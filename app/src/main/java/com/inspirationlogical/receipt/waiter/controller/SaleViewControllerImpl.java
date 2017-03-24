package com.inspirationlogical.receipt.waiter.controller;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import com.inspirationlogical.receipt.waiter.viewstate.SaleViewState;

import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.waiter.controller.SaleViewElementControllerImpl.SALE_VIEW_ELEMENT_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;

/**
 * Created by Bálint on 2017.03.22..
 */
@Singleton
public class SaleViewControllerImpl implements SaleViewController {

    public static final String SALE_VIEW_PATH = "/view/fxml/SaleView.fxml";

    @FXML
    BorderPane root;

    @FXML
    AnchorPane center;

    @FXML
    VBox left;

    @FXML
    Label totalPrice;

    @FXML
    GridPane categoriesGrid;

    @FXML
    GridPane subCategoriesGrid;

    @FXML
    GridPane productsGrid;

    @FXML
    javafx.scene.control.TableView<SoldProductsTableModel> soldProductsTable;

    @FXML
    TableColumn productName;

    @FXML
    TableColumn productQuantity;

    @FXML
    TableColumn productUnitPrice;

    @FXML
    TableColumn productTotalPrice;

    @FXML
    Button backToRestaurantView;

    private RestaurantController restaurantController;

    private SaleViewState saleViewState;

    private RestaurantServices restaurantServices;

    private RetailServices retailServices;

    private @Setter TableView tableView;

    private ReceiptView receiptView;

    private Collection<ReceiptRecordView> soldProducts;

    private ProductCategoryView rootCategory;

    private ProductCategoryView selectedCategory;

    private List<ProductCategoryView> selectedLevelCategories;

    private List<ProductCategoryView> selectedChildrenCategories;

    private List<ProductView> visibleProducts;

    private List<SaleViewElementController> elementControllers;

    @Inject
    public SaleViewControllerImpl(RetailServices retailServices,
                                  RestaurantServices restaurantServices,
                                  RestaurantController restaurantController) {
        this.retailServices = retailServices;
        this.restaurantServices = restaurantServices;
        this.restaurantController = restaurantController;
        this.elementControllers = new ArrayList<>();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSoldProducts(restaurantServices, tableView);
        initializeCategories(restaurantServices);
        initializeSaleViewState(restaurantController);
        updateNode();
    }

    private void updateNode() {
        updateSoldProductsTable();
        updateCategories();
    }

    private void updateCategories() {
        drawListOfElements(selectedLevelCategories, categoriesGrid);
        drawListOfElements(selectedChildrenCategories, subCategoriesGrid);
        drawListOfElements(visibleProducts, productsGrid);
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        Parent root = (Parent) restaurantController.getRootNode();
        Main.getWindow().getScene().setRoot(root);
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    private void initializeSoldProducts(RestaurantServices restaurantServices, TableView tableView) {
        receiptView = restaurantServices.getActiveReceipt(tableView);
        soldProducts = receiptView.getSoldProducts();
    }

    private void initializeCategories(RestaurantServices restaurantServices) {
        rootCategory = restaurantServices.getRootProductCategory();
        selectedLevelCategories = rootCategory.getChildrenCategories();
        selectedCategory = selectedLevelCategories.get(0);
        selectedChildrenCategories = selectedCategory.getChildrenCategories();
        visibleProducts = selectedCategory.getAllProducts();
    }

    private void initializeSaleViewState(RestaurantController restaurantController) {
        saleViewState = new SaleViewState();
        saleViewState.setFullScreen(restaurantController.getViewState().isFullScreen());
    }

    private void updateSoldProductsTable() {
        soldProductsTable.setEditable(true);
        productName.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productName"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productQuantity"));
        productUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productUnitPrice"));
        productTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productTotalPrice"));
        ObservableList<SoldProductsTableModel> soldProductList = FXCollections.observableArrayList();
        soldProductList.addAll(soldProducts.stream()
                .map(receiptRecordView -> new SoldProductsTableModel(receiptRecordView.getName(),
                        String.valueOf(receiptRecordView.getSoldQuantity()),
                        String.valueOf(receiptRecordView.getSalePrice()),
                        String.valueOf(receiptRecordView.getTotalPrice())))
                .collect(Collectors.toList()));
        soldProductsTable.setItems(soldProductList);
    }

    private <T extends AbstractView> void drawListOfElements(List<T> elements, GridPane grid) {
        for(int i = 0; i < elements.size(); i++) {
            drawElement(elements.get(i), grid, i);
        }
    }

    private <T extends AbstractView> void drawElement(T elementView, GridPane grid, int index) {
        SaleViewElementController<T> elementController =
                getInjector().getInstance(SaleViewElementController.class);
        elementController.setView(elementView);
        elementControllers.add(elementController);
        Node root = loadView(SALE_VIEW_ELEMENT_PATH, elementController);
        grid.add(elementController.getRootNode(), index % 4, index / 4);
    }
}
