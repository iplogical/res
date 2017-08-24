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
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.PriceModifierController;
import com.inspirationlogical.receipt.manager.controller.receipt.ReceiptController;
import com.inspirationlogical.receipt.manager.controller.stock.StockController;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryViewModel;

import com.inspirationlogical.receipt.manager.viewmodel.ProductViewModel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;

@Singleton
public class GoodsControllerImpl extends AbstractController implements GoodsController {

    private static final String GOODS_VIEW_PATH = "/view/fxml/Goods.fxml";

    @FXML
    private BorderPane root;
    @FXML
    private TreeTableView<CategoryViewModel> goodsTable;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> categoryName;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productShortName;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productRapidCode;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productOrderNumber;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productType;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productStatus;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productQuantityUnit;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productStorageMultiplier;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productQuantityMultiplier;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productPurchasePrice;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productSalePrice;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productMinimumStock;
    @FXML
    private TreeTableColumn<CategoryViewModel, String> productStockWindow;
    @FXML
    private Button addCategory;
    @FXML
    private Button modifyCategory;
    @FXML
    private Button deleteCategory;
    @FXML
    private Button addProduct;
    @FXML
    private Button modifyProduct;
    @FXML
    private Button deleteProduct;
    @FXML
    private Button showStock;
    @FXML
    private Button showPriceModifiers;
    @FXML
    private Button showReceipts;

    @Inject
    private ViewLoader viewLoader;

    private StockController stockController;

    private PriceModifierController priceModifierController;

    private ReceiptController receiptController;

    private CommonService commonService;

    private Popup productForm;

    private ProductFormController productFormController;

    private Popup categoryForm;

    private CategoryFormController categoryFormController;

    private Popup recipeForm;

    private RecipeFormController recipeFormController;

    private ProductCategoryView rootCategory;

    @Inject
    public GoodsControllerImpl(StockController stockController,
                               PriceModifierController priceModifierController,
                               ReceiptController receiptController,
                               ProductFormController productFormController,
                               CategoryFormController categoryFormController,
                               RecipeFormController recipeFormController,
                               CommonService commonService) {
        this.stockController = stockController;
        this.priceModifierController = priceModifierController;
        this.receiptController = receiptController;
        this.productFormController = productFormController;
        this.categoryFormController = categoryFormController;
        this.recipeFormController = recipeFormController;
        this.commonService = commonService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initCategories();
        initForms();
    }

    private void initColumns() {
        initColumn(categoryName, CategoryViewModel::getName);
        initColumn(productShortName, CategoryViewModel::getShortName);
        initColumn(productRapidCode, CategoryViewModel::getRapidCode);
        initColumn(productOrderNumber, CategoryViewModel::getOrderNumber);
        initColumn(productPurchasePrice, CategoryViewModel::getPurchasePrice);
        initColumn(productSalePrice, CategoryViewModel::getSalePrice);
        initColumn(productStorageMultiplier, CategoryViewModel::getStorageMultiplier);
        initColumn(productQuantityUnit, CategoryViewModel::getQuantityUnit);
        initColumn(productQuantityMultiplier, CategoryViewModel::getQuantityMultiplier);
        initColumn(productMinimumStock, CategoryViewModel::getMinimumStock);
        initColumn(productStockWindow, CategoryViewModel::getStockWindow);
        initColumn(productType, CategoryViewModel::getType);
        initColumn(productStatus, CategoryViewModel::getStatus);
    }

    private void initCategories() {
        rootCategory = commonService.getRootProductCategory();
        TreeItem<CategoryViewModel> rootItem = new TreeItem<>(new CategoryViewModel(rootCategory));
        goodsTable.setRoot(rootItem);
        goodsTable.setShowRoot(false);
        updateCategory(rootCategory, rootItem);
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

    @FXML
    public void initRecipeForm() {
        recipeForm = new Popup();
        recipeForm.getContent().add(viewLoader.loadView(recipeFormController));
        recipeFormController.loadRecipeForm(this);
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
            commonService.addProduct(parent, builder);
        } else {
            commonService.updateProduct(productId, parent, builder);
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
            commonService.addProductCategory(params);
        } else {
            commonService.updateProductCategory(params);
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
        ProductViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        CategoryViewModel parent = goodsTable.getSelectionModel().getSelectedItem().getParent().getValue();
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

    private boolean isCategorySelected(ProductViewModel selected) {
        return selected.getLongName().equals("");
    }

    @FXML
    public void onDeleteProduct(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForDelete"));
            return;
        }
        ProductViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        if(isCategorySelected(selected)) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForDelete"));
            return;
        }
        commonService.deleteProduct(selected.getLongName());
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
        CategoryViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        if(isProductSelected(selected)) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForModify"));
            return;
        }
        initCategoryForm();
        categoryFormController.setCategory(goodsTable.getSelectionModel().getSelectedItem().getValue());
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    private boolean isProductSelected(CategoryViewModel selected) {
        return !selected.getLongName().equals("");
    }

    @FXML
    public void onDeleteCategory(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForDelete"));
            return;
        }
        CategoryViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        if(isProductSelected(selected)) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForDelete"));
            return;
        }
        commonService.deleteProductCategory(selected.getName());
        initCategories();
    }

    @FXML
    public void onCreateRecipe(Event event) {
        recipeFormController.fetchProducts();
        showPopup(recipeForm, recipeFormController, root, new Point2D(520, 200));
    }

    private void updateCategory(ProductCategoryView productCategoryView, TreeItem<CategoryViewModel> treeItem) {
        treeItem.setExpanded(true);
        commonService.getChildCategories(productCategoryView).forEach(child -> {
            if (child.getStatus() == ProductStatus.ACTIVE) {
                CategoryViewModel categoryViewModel = new CategoryViewModel(child);
                TreeItem<CategoryViewModel> childItem = new TreeItem<>(categoryViewModel);
                treeItem.getChildren().add(childItem);
                sortTreeItemChildren(treeItem, childItem);
                updateProduct(categoryViewModel, childItem);
                updateCategory(child, childItem);
            }
        });
    }

    private void sortTreeItemChildren(TreeItem<CategoryViewModel> treeItem, TreeItem<CategoryViewModel> childItem) {
        if(childItem.getValue().getName().isEmpty())
            treeItem.getChildren().sort(Comparator.comparing(categoryViewModelTreeItem -> categoryViewModelTreeItem.getValue().getLongName()));
        else
            treeItem.getChildren().sort(Comparator.comparing(categoryViewModelTreeItem -> categoryViewModelTreeItem.getValue().getName()));
    }

    private void updateProduct(CategoryViewModel categoryViewModel, TreeItem<CategoryViewModel> productItem) {
        categoryViewModel.getRecipes().forEach(recipe -> {
            TreeItem<CategoryViewModel> recipeItem = new TreeItem<>(new CategoryViewModel(recipe.getComponent()));
            productItem.getChildren().add(recipeItem);
        });
    }

    private boolean isSelectionNull() {
        return goodsTable.getSelectionModel().getSelectedItem() == null;
    }
}
