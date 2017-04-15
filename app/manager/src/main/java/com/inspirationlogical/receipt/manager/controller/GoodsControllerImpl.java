package com.inspirationlogical.receipt.manager.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.manager.viewmodel.CategoryViewModel;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;

@Singleton
public class GoodsControllerImpl implements GoodsController {

    public static final String GOODS_VIEW_PATH = "/view/fxml/Goods.fxml";

    @FXML
    private BorderPane root;
    @FXML
    TreeTableView<CategoryViewModel> goodsTable;
    @FXML
    TreeTableColumn<CategoryViewModel, String> categoryName;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productShortName;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productRapidCode;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productType;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productStatus;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productQuantityUnit;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productStorageMultiplier;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productQuantityMultiplier;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productPurchasePrice;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productSalePrice;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productMinimumStock;
    @FXML
    TreeTableColumn<CategoryViewModel, String> productStockWindow;
    @FXML
    Button createCategory;
    @FXML
    Button modifyCategory;
    @FXML
    Button deleteCategory;
    @FXML
    Button createProduct;
    @FXML
    Button modifyProduct;
    @FXML
    Button deleteProduct;
    @FXML
    Button showStock;
    @FXML
    Button showPriceModifier;

    @Inject
    private ViewLoader viewLoader;

    private StockController stockController;

    private PriceModifierController priceModifierController;

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
                               ProductFormController productFormController,
                               CategoryFormController categoryFormController,
                               RecipeFormController recipeFormController,
                               CommonService commonService) {
        this.stockController = stockController;
        this.priceModifierController = priceModifierController;
        this.productFormController = productFormController;
        this.categoryFormController = categoryFormController;
        this.recipeFormController = recipeFormController;
        this.commonService = commonService;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        initCategories();
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
    public void addProduct(ProductCategoryView parent, Product.ProductBuilder builder) {
        try {
            commonService.addProduct(parent, builder);
            initCategories();
            productForm.hide();
        } catch (IllegalProductStateException e ) {
            ErrorMessage.showErrorMessage(root, e.getMessage());
            initColumns();
            initCategories();
            productForm.hide();
        } catch (Exception e) {
            initColumns();
            initCategories();
            categoryForm.hide();
        }
    }

    @Override
    public void addProductCategory(ProductCategoryParams params) {
        try {
            if(params.getOriginalName().equals("")) {
                commonService.addProductCategory(params);
            } else {
                commonService.updateProductCategory(params);
            }
            initCategories();
            categoryForm.hide();
        } catch (IllegalProductCategoryStateException e) {
            ErrorMessage.showErrorMessage(root, e.getMessage());
            initColumns();
            initCategories();
            categoryForm.hide();
        } catch (Exception e) {
            initColumns();
            initCategories();
            categoryForm.hide();
        }
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
    public void onCreateProduct(Event event) {
        productForm = new Popup();
        productForm.getContent().add(viewLoader.loadView(productFormController));
        productFormController.loadProductForm(this);
        showPopup(productForm, productFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyProduct(Event event) {
    }

    @FXML
    public void onDeleteProduct(Event event) {
    }

    @FXML
    public void onCreateCategory(Event event) {
        initProductCategoryForm();
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyCategory(Event event) {
        if(goodsTable.getSelectionModel().getSelectedItem() == null) return;
        CategoryViewModel selected = goodsTable.getSelectionModel().getSelectedItem().getValue();
        initProductCategoryForm();
        if(selected.getName().equals("root")) return;
        categoryFormController.setCategory(goodsTable.getSelectionModel().getSelectedItem().getValue());
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onDeleteCategory(Event event) {
    }

    @FXML
    public void onCreateRecipe(Event event) {
        recipeForm = new Popup();
        recipeForm.getContent().add(viewLoader.loadView(recipeFormController));
        recipeFormController.loadRecipeForm(this);
        showPopup(recipeForm, recipeFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyRecipe(Event event) {
    }

    @FXML
    public void onDeleteRecipe(Event event) {
    }

    private CategoryViewModel getViewModel(CellDataFeatures<CategoryViewModel, String> cellDataFeatures) {
        return cellDataFeatures.getValue().getValue();
    }

    private void initColumn(TreeTableColumn<CategoryViewModel, String> treeTableColumn, Function<CategoryViewModel, String> method) {
        treeTableColumn.setCellValueFactory((CellDataFeatures<CategoryViewModel, String> category) ->
                new ReadOnlyStringWrapper(method.apply(getViewModel(category))));
    }

    private void initColumns() {
        initColumn(categoryName, CategoryViewModel::getName);
        initColumn(productShortName, CategoryViewModel::getShortName);
        initColumn(productRapidCode, CategoryViewModel::getRapidCode);
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

        updateCategory(rootCategory, rootItem);
    }

    private void updateCategory(ProductCategoryView productCategoryView, TreeItem<CategoryViewModel> treeItem) {
        treeItem.setExpanded(true);
        productCategoryView.getChildrenCategories().forEach(child -> {
            CategoryViewModel categoryViewModel = new CategoryViewModel(child);
            TreeItem<CategoryViewModel> childItem = new TreeItem<>(categoryViewModel);
            treeItem.getChildren().add(childItem);
            updateProduct(categoryViewModel, childItem);
            updateCategory(child, childItem);
        });
    }

    private void updateProduct(CategoryViewModel categoryViewModel, TreeItem<CategoryViewModel> productItem) {
        categoryViewModel.getRecipes().forEach(recipe -> {
            TreeItem<CategoryViewModel> recipeItem = new TreeItem<>(new CategoryViewModel(recipe.getComponent()));
            productItem.getChildren().add(recipeItem);
        });
    }

    private void initProductCategoryForm() {
        categoryForm = new Popup();
        categoryForm.getContent().add(viewLoader.loadView(categoryFormController));
        categoryFormController.loadProductCategoryForm(this);
    }
}
