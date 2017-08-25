package com.inspirationlogical.receipt.manager.controller.goods;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.PriceModifierController;
import com.inspirationlogical.receipt.manager.controller.receipt.ReceiptController;
import com.inspirationlogical.receipt.manager.controller.stock.StockController;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;

import com.inspirationlogical.receipt.manager.viewmodel.GoodsTableViewModel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;

@Singleton
public class GoodsControllerImpl extends AbstractController implements GoodsController {

    private static final String GOODS_VIEW_PATH = "/view/fxml/Goods.fxml";

    private @FXML BorderPane root;

    private @FXML TreeTableView<GoodsTableViewModel> goodsTable;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> categoryName;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productShortName;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productRapidCode;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productOrderNumber;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productType;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productStatus;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productQuantityUnit;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productStorageMultiplier;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productQuantityMultiplier;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productPurchasePrice;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productSalePrice;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productMinimumStock;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> productStockWindow;

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

    private @Inject ViewLoader viewLoader;

    private @Inject StockController stockController;
    private @Inject PriceModifierController priceModifierController;
    private @Inject ReceiptController receiptController;

    private @Inject CommonService commonService;
    private @Inject ManagerService managerService;

    private Popup productForm;
    private @Inject ProductFormController productFormController;

    private Popup categoryForm;
    private @Inject CategoryFormController categoryFormController;

    private Popup recipeForm;
    private @Inject RecipeFormController recipeFormController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initCheckBox();
        initForms();
        initCategories();
    }

    private void initCheckBox() {
        showDeleted.selectedProperty().addListener((observable, oldValue, newValue) -> {
            initCategories();
        });
    }

    private void initColumns() {
        initColumn(categoryName, GoodsTableViewModel::getName);
        initColumn(productShortName, GoodsTableViewModel::getShortName);
        initColumn(productRapidCode, GoodsTableViewModel::getRapidCode);
        initColumn(productOrderNumber, GoodsTableViewModel::getOrderNumber);
        initColumn(productPurchasePrice, GoodsTableViewModel::getPurchasePrice);
        initColumn(productSalePrice, GoodsTableViewModel::getSalePrice);
        initColumn(productStorageMultiplier, GoodsTableViewModel::getStorageMultiplier);
        initColumn(productQuantityUnit, GoodsTableViewModel::getQuantityUnit);
        initColumn(productQuantityMultiplier, GoodsTableViewModel::getQuantityMultiplier);
        initColumn(productMinimumStock, GoodsTableViewModel::getMinimumStock);
        initColumn(productStockWindow, GoodsTableViewModel::getStockWindow);
        initColumn(productType, GoodsTableViewModel::getType);
        initColumn(productStatus, GoodsTableViewModel::getStatus);
    }

    private void initForms() {
        initCategoryForm();
        initProductForm();
        initRecipeForm();
    }

    private void initCategoryForm() {
        categoryForm = new Popup();
        categoryForm.getContent().add(viewLoader.loadView(categoryFormController));
        categoryFormController.loadCategoryForm(this);
    }

    private void initProductForm() {
        productForm = new Popup();
        productForm.getContent().add(viewLoader.loadView(productFormController));
        productFormController.loadProductForm(this);
    }

    private void initRecipeForm() {
        recipeForm = new Popup();
        recipeForm.getContent().add(viewLoader.loadView(recipeFormController));
        recipeFormController.loadRecipeForm(this);
    }

    private void initCategories() {
        ProductCategoryView rootCategory = commonService.getRootProductCategory();
        GoodsTableViewModel goodsTableViewModel = createProductViewModel(rootCategory);
        TreeItem<GoodsTableViewModel> rootItem = new TreeItem<>(goodsTableViewModel);
        goodsTable.setRoot(rootItem);
        goodsTable.setShowRoot(false);
        updateCategory(rootCategory, rootItem);
    }

    private void updateCategory(ProductCategoryView productCategoryView, TreeItem<GoodsTableViewModel> parentTreeItem) {
        parentTreeItem.setExpanded(true);
        commonService.getChildCategories(productCategoryView).forEach(childCategory -> {
            if (showActiveAndDeletedIfAllowed(childCategory)) {
                TreeItem<GoodsTableViewModel> childTreeItem = addProductsAndRecipeItems(parentTreeItem, childCategory);
                updateCategory(childCategory, childTreeItem);
            }
        });
    }

    private boolean showActiveAndDeletedIfAllowed(ProductCategoryView childCategory) {
        return childCategory.getStatus() == ProductStatus.ACTIVE || showDeleted.isSelected();
    }

    private TreeItem<GoodsTableViewModel> addProductsAndRecipeItems(TreeItem<GoodsTableViewModel> parentTreeItem, ProductCategoryView childCategory) {
        GoodsTableViewModel goodsTableViewModel = createProductViewModel(childCategory);
        TreeItem<GoodsTableViewModel> childTreeItem = new TreeItem<>(goodsTableViewModel);
        parentTreeItem.getChildren().add(childTreeItem);
        sortTreeItemChildren(parentTreeItem);
        addRecipeItemsToProduct(goodsTableViewModel, childTreeItem);
        return childTreeItem;
    }

    private GoodsTableViewModel createProductViewModel(ProductCategoryView childCategory) {
        GoodsTableViewModel goodsTableViewModel = null;
        ProductView productView = childCategory.getProduct();
        if(productView != null) {
            goodsTableViewModel = new GoodsTableViewModel(productView);
        } else {
            goodsTableViewModel = new GoodsTableViewModel();
            goodsTableViewModel.setName(childCategory.getName());
            goodsTableViewModel.setOrderNumber(String.valueOf(childCategory.getOrderNumber()));
            goodsTableViewModel.setStatus(childCategory.getStatus().toI18nString());
        }
        return goodsTableViewModel;
    }

    private void sortTreeItemChildren(TreeItem<GoodsTableViewModel> treeItem) {
        treeItem.getChildren().sort(Comparator.comparing(categoryViewModelTreeItem -> categoryViewModelTreeItem.getValue().getName()));
    }

    private void addRecipeItemsToProduct(GoodsTableViewModel goodsTableViewModel, TreeItem<GoodsTableViewModel> productItem) {
        goodsTableViewModel.getRecipes().forEach(recipe -> {
            TreeItem<GoodsTableViewModel> recipeItem = new TreeItem<>(new GoodsTableViewModel(recipe.getComponent()));
            productItem.getChildren().add(recipeItem);
        });
    }

    @Override
    public String getViewPath() {
        return GOODS_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void addProduct(Long productId, ProductCategoryView parent, Product.ProductBuilder builder) {
        try {
            addOrUpdateProduct(productId, parent, builder);
        } catch (IllegalProductStateException e ) {
            ErrorMessage.showErrorMessage(root, e.getMessage());
        } finally {
            productForm.hide();
            initCategories();
        }
    }

    private void addOrUpdateProduct(Long productId, ProductCategoryView parent, Product.ProductBuilder builder) {
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
            initCategories();
        } catch (IllegalProductCategoryStateException e) {
            ErrorMessage.showErrorMessage(root, e.getMessage());
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
        initCategories();
    }

    @FXML
    public void onShowStock(Event event) {
        viewLoader.loadViewIntoScene(stockController);
    }

    @FXML
    public void onShowPriceModifiers(Event event) {
        viewLoader.loadViewIntoScene(priceModifierController);
    }

    @FXML
    public void onShowReceipts(Event event) {
        viewLoader.loadViewIntoScene(receiptController);
    }

    @FXML
    public void onCreateProduct(Event event) {
        initProductForm();
        showPopup(productForm, productFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyProduct(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForModify"));
            return;
        }
        GoodsTableViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        GoodsTableViewModel parent = goodsTable.getSelectionModel().getSelectedItem().getParent().getValue();
        if(isCategorySelected(selected)) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForModify"));
            return;
        }
        initProductForm();
        productFormController.setProductViewModel(selected);
        productFormController.setCategory(parent);
        showPopup(productForm, productFormController, root, new Point2D(520, 200));
    }

    private boolean isCategorySelected(GoodsTableViewModel selected) {
        return selected.getShortName().equals("");
    }

    @FXML
    public void onDeleteProduct(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForDelete"));
            return;
        }
        GoodsTableViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        if(isCategorySelected(selected)) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForDelete"));
            return;
        }
        managerService.deleteProduct(selected.getName());
        initCategories();
    }

    @FXML
    public void onCreateCategory(Event event) {
        initCategoryForm();
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyCategory(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForModify"));
            return;
        }
        GoodsTableViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        if(isProductSelected(selected)) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForModify"));
            return;
        }
        initCategoryForm();
        categoryFormController.setCategory(goodsTable.getSelectionModel().getSelectedItem().getValue());
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    private boolean isProductSelected(GoodsTableViewModel selected) {
        return !selected.getShortName().equals("");
    }

    @FXML
    public void onDeleteCategory(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForDelete"));
            return;
        }
        GoodsTableViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        if(isProductSelected(selected)) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForDelete"));
            return;
        }
        managerService.deleteProductCategory(selected.getName());
        initCategories();
    }

    @FXML
    public void onCreateRecipe(Event event) {
        recipeFormController.fetchProducts();
        showPopup(recipeForm, recipeFormController, root, new Point2D(520, 200));
    }

    private boolean isSelectionNull() {
        return goodsTable.getSelectionModel().getSelectedItem() == null;
    }
}
