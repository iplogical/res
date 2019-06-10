package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.utility.NotificationMessage;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.PriceModifierFxmlView;
import com.inspirationlogical.receipt.manager.controller.receipt.ReceiptFxmlView;
import com.inspirationlogical.receipt.manager.controller.stock.StockFxmlView;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;

@FXMLController
public class GoodsControllerImpl extends AbstractController implements GoodsController {

    private @FXML BorderPane root;

    private @FXML TreeTableView<ProductCategoryView> categoriesTable;
    private @FXML TreeTableColumn<ProductCategoryView, String> categoryName;

    private @FXML TableView<ProductView> productsTable;
    private @FXML TableColumn<ProductView, String> productShortName;
    private @FXML TableColumn<ProductView, String> productRapidCode;
    private @FXML TableColumn<ProductView, String> productOrderNumber;
    private @FXML TableColumn<ProductView, String> productType;
    private @FXML TableColumn<ProductView, String> productStatus;
    private @FXML TableColumn<ProductView, String> productQuantityUnit;
    private @FXML TableColumn<ProductView, String> productStorageMultiplier;
    private @FXML TableColumn<ProductView, String> productPurchasePrice;
    private @FXML TableColumn<ProductView, String> productSalePrice;
    private @FXML TableColumn<ProductView, String> productMinimumStock;
    private @FXML TableColumn<ProductView, String> productStockWindow;

    private @FXML CheckBox showDeleted;
    private @FXML Button addCategory;
    private @FXML Button modifyCategory;
    private @FXML Button deleteCategory;

    private @FXML Button addProduct;
    private @FXML Button modifyProduct;
    private @FXML Button deleteProduct;

    private @FXML Button showStock;
    private @FXML Button showPriceModifiers;
    private @FXML Button showReceipts;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ManagerService managerService;

    private Popup productForm;

    @Autowired
    private ProductFormController productFormController;

    private Popup categoryForm;

    @Autowired
    private CategoryFormController categoryFormController;

    private Popup recipeForm;

    @Autowired
    private RecipeFormController recipeFormController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initCheckBox();
        initForms();
        initCategories();
    }

    private void initColumns() {
        initColumn(categoryName, ProductCategoryView::getName);
        initColumn(productShortName, ProductView::getShortName);
        initColumn(productRapidCode, productView -> String.valueOf(productView.getRapidCode()));
        initColumn(productOrderNumber, productView -> String.valueOf(productView.getOrderNumber()));
        initColumn(productPurchasePrice, productView -> String.valueOf(productView.getPurchasePrice()));
        initColumn(productSalePrice, productView -> String.valueOf(productView.getSalePrice()));
        initColumn(productStorageMultiplier, productView -> String.valueOf(productView.getStorageMultiplier()));
        initColumn(productQuantityUnit, productView -> productView.getQuantityUnit().toI18nString());
        initColumn(productMinimumStock, productView -> String.valueOf(productView.getMinimumStock()));
        initColumn(productStockWindow, productView -> String.valueOf(productView.getStockWindow()));
        initColumn(productType, productView -> productView.getType().toI18nString());
        initColumn(productStatus, productView -> productView.getStatus().toI18nString());
    }

    private void initCheckBox() {
        showDeleted.selectedProperty().addListener((observable, oldValue, newValue) -> {
            ProductCategoryView selectedCategory = getSelectedCategory();
            if (selectedCategory != null) {
                refreshProductsTable(selectedCategory);
            }
        });
    }

    private void initForms() {
        initCategoryForm();
        initProductForm();
        initRecipeForm();
    }

    private void initCategoryForm() {
        categoryForm = new Popup();
        categoryForm.getContent().add(ManagerApp.getRootNode(CategoryFormFxmlView.class));
        categoryFormController.loadCategoryForm(this);
    }

    private void initProductForm() {
        productForm = new Popup();
        productForm.getContent().add(ManagerApp.getRootNode(ProductFormFxmlView.class));
        productFormController.loadProductForm(this);
    }

    private void initRecipeForm() {
        recipeForm = new Popup();
        recipeForm.getContent().add(ManagerApp.getRootNode(RecipeFormFxmlView.class));
        recipeFormController.loadRecipeForm(this);
    }

    private void initCategories() {
        ProductCategoryView rootCategory = commonService.getRootProductCategory();
        TreeItem<ProductCategoryView> rootItem = new TreeItem<>(rootCategory);
        categoriesTable.setRoot(rootItem);
        categoriesTable.setShowRoot(false);
        updateCategory(rootCategory, rootItem);
        categoriesTable.getSelectionModel().selectedItemProperty().addListener(this::onCategoriesTableSelectionChanged);
    }

    private void onCategoriesTableSelectionChanged(ObservableValue<? extends TreeItem<ProductCategoryView>> obs,
                                           TreeItem<ProductCategoryView> oldSelection,
                                           TreeItem<ProductCategoryView> newSelection) {
        if (newSelection != null) {
            refreshProductsTable(newSelection.getValue());
        }
    }

    private void refreshProductsTable(ProductCategoryView selectedCategory) {
        List<ProductView> productViewList = managerService.getProductsByCategory(selectedCategory, showDeleted.isSelected());
        ObservableList<ProductView> productViewObservableList = FXCollections.observableArrayList(productViewList);
        productsTable.setItems(productViewObservableList);
        productsTable.refresh();
    }

    private void updateCategory(ProductCategoryView productCategoryView, TreeItem<ProductCategoryView> parentTreeItem) {
        parentTreeItem.setExpanded(true);
        commonService.getChildCategories(productCategoryView).forEach(childCategory -> {
            if (showActiveAndDeletedIfAllowed(childCategory)) {
                TreeItem<ProductCategoryView> childTreeItem = addProductsAndRecipeItems(parentTreeItem, childCategory);
                updateCategory(childCategory, childTreeItem);
            }
        });
    }

    private boolean showActiveAndDeletedIfAllowed(ProductCategoryView childCategory) {
        return childCategory.getStatus() == ProductStatus.ACTIVE || showDeleted.isSelected();
    }

    private TreeItem<ProductCategoryView> addProductsAndRecipeItems(TreeItem<ProductCategoryView> parentTreeItem, ProductCategoryView childCategory) {
        TreeItem<ProductCategoryView> childTreeItem = new TreeItem<>(childCategory);
        parentTreeItem.getChildren().add(childTreeItem);
        sortTreeItemChildren(parentTreeItem);
        return childTreeItem;
    }

    private void sortTreeItemChildren(TreeItem<ProductCategoryView> treeItem) {
        treeItem.getChildren().sort(Comparator.comparing(categoryViewModelTreeItem -> categoryViewModelTreeItem.getValue().getName()));
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void addProduct(int productId, ProductCategoryView parent, Product.ProductBuilder builder) {
        try {
            addOrUpdateProduct(productId, parent, builder);
        } catch (IllegalProductStateException e ) {
            NotificationMessage.showErrorMessage(root, e.getMessage());
        } finally {
            productForm.hide();
            refreshProductsTable(getSelectedCategory());
        }
    }

    private void initCategoriesAndScrollBack() {
        int index = 0;
        if (categoriesTable.getSelectionModel() != null) {
            index = categoriesTable.getSelectionModel().getFocusedIndex();
        }
        initCategories();
        categoriesTable.scrollTo(index);
    }

    private void addOrUpdateProduct(int productId, ProductCategoryView parent, Product.ProductBuilder builder) {
        if(productId == 0) {
            managerService.addProduct(parent, builder);
        } else {
            managerService.updateProduct(productId, parent, builder);
        }
    }

    @Override
    public void addCategory(ProductCategoryParams params) {
        try {
            addOrUpdateCategory(params);
            initCategoriesAndScrollBack();
        } catch (IllegalProductCategoryStateException e) {
            NotificationMessage.showErrorMessage(root, e.getMessage());
        }  finally {
            categoryForm.hide();
        }
    }

    private void addOrUpdateCategory(ProductCategoryParams params) {
        if(params.getOriginalName().equals("")) {
            managerService.addProductCategory(params);
        } else {
            managerService.updateProductCategory(params);
        }
    }

    @Override
    public void updateGoods() {
        initCategoriesAndScrollBack();
    }

    @Override
    public void hideCategoryForm() {
        categoryForm.hide();
    }

    @Override
    public void hideProductForm() {
        productForm.hide();
    }

    @Override
    public void hideRecipeForm() {
        recipeForm.hide();
    }

    @FXML
    public void onShowStock(Event event) {
        ManagerApp.showView(StockFxmlView.class);
    }

    @FXML
    public void onShowPriceModifiers(Event event) {
        ManagerApp.showView(PriceModifierFxmlView.class);
    }

    @FXML
    public void onShowReceipts(Event event) {
        ManagerApp.showView(ReceiptFxmlView.class);
    }

    @FXML
    public void onCreateProduct(Event event) {
        initProductForm();
        showPopup(productForm, productFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyProduct(Event event) {
        ProductView selected = getSelectedProduct();
        if(selected == null) {
            NotificationMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForModify"));
            return;
        }
        ProductCategoryView parent = categoriesTable.getSelectionModel().getSelectedItem().getValue();
        initProductForm();
        productFormController.setProductViewModel(selected);
        productFormController.setCategory(parent);
        showPopup(productForm, productFormController, root, new Point2D(520, 200));
    }

    private ProductView getSelectedProduct() {
        return productsTable.getSelectionModel().getSelectedItem();
    }

    private ProductCategoryView getSelectedCategory() {
        return categoriesTable.getSelectionModel().getSelectedItem().getValue();
    }

    @FXML
    public void onDeleteProduct(Event event) {
        ProductView selected = getSelectedProduct();
        if(selected == null) {
            NotificationMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForDelete"));
            return;
        }
        managerService.deleteProduct(selected.getName());
        refreshProductsTable(getSelectedCategory());
    }

    @FXML
    public void onCreateCategory(Event event) {
        initCategoryForm();
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyCategory(Event event) {
        ProductCategoryView selected = getSelectedCategory();
        if(selected  == null) {
            NotificationMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForModify"));
            return;
        }
        initCategoryForm();
        categoryFormController.setCategory(selected);
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onDeleteCategory(Event event) {
        ProductCategoryView selected = getSelectedCategory();
        if(selected  == null) {
            NotificationMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForDelete"));
            return;
        }
        managerService.deleteProductCategory(selected.getName());
        initCategories();
    }

    @FXML
    public void onShowRecipeForm(Event event) {
        ProductView selected = getSelectedProduct();
        if(selected != null) {
            //TODO: recipe form

//                recipeFormController.setSelectedProduct(selected);
        } else {
            recipeFormController.updateComponentsTable();
        }
        showPopup(recipeForm, recipeFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onRefreshRecipes(Event event) {
        recipeFormController.initProducts();
    }
}
