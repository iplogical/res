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
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewmodel.GoodsTableViewModel;
import com.inspirationlogical.receipt.manager.controller.stock.*;
import com.inspirationlogical.receipt.manager.controller.pricemodifier.*;
import com.inspirationlogical.receipt.manager.controller.receipt.*;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventType;
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

    private @FXML TreeTableView<GoodsTableViewModel> categoriesTable;
    private @FXML TreeTableColumn<GoodsTableViewModel, String> categoryName;

    private @FXML TableView<GoodsTableViewModel> productsTable;
    private @FXML TableColumn<GoodsTableViewModel, String> productShortName;
    private @FXML TableColumn<GoodsTableViewModel, String> productRapidCode;
    private @FXML TableColumn<GoodsTableViewModel, String> productOrderNumber;
    private @FXML TableColumn<GoodsTableViewModel, String> productType;
    private @FXML TableColumn<GoodsTableViewModel, String> productStatus;
    private @FXML TableColumn<GoodsTableViewModel, String> productQuantityUnit;
    private @FXML TableColumn<GoodsTableViewModel, String> productStorageMultiplier;
    private @FXML TableColumn<GoodsTableViewModel, String> productQuantityMultiplier;
    private @FXML TableColumn<GoodsTableViewModel, String> productPurchasePrice;
    private @FXML TableColumn<GoodsTableViewModel, String> productSalePrice;
    private @FXML TableColumn<GoodsTableViewModel, String> productMinimumStock;
    private @FXML TableColumn<GoodsTableViewModel, String> productStockWindow;

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
        GoodsTableViewModel goodsTableViewModel = createGoodsTableViewModel(rootCategory);
        TreeItem<GoodsTableViewModel> rootItem = new TreeItem<>(goodsTableViewModel);
        categoriesTable.setRoot(rootItem);
        categoriesTable.setShowRoot(false);
        updateCategory(rootCategory, rootItem);
        categoriesTable.getSelectionModel().selectedItemProperty().addListener(this::onCategoriesTableSelectionChanged);
    }

    private void onCategoriesTableSelectionChanged(ObservableValue<? extends TreeItem<GoodsTableViewModel>> obs,
                                           TreeItem<GoodsTableViewModel> oldSelection,
                                           TreeItem<GoodsTableViewModel> newSelection) {
        if (newSelection != null) {
            List<ProductView> productViewList = commonService.getSellableProducts();
        }
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
        GoodsTableViewModel goodsTableViewModel = createGoodsTableViewModel(childCategory);
        TreeItem<GoodsTableViewModel> childTreeItem = new TreeItem<>(goodsTableViewModel);
        parentTreeItem.getChildren().add(childTreeItem);
        sortTreeItemChildren(parentTreeItem);
        return childTreeItem;
    }

    private GoodsTableViewModel createGoodsTableViewModel(ProductCategoryView childCategory) {
        GoodsTableViewModel goodsTableViewModel;
        ProductView productView = childCategory.getProduct();
        if(productView != null) {
            goodsTableViewModel = new GoodsTableViewModel(productView);
        } else {
            goodsTableViewModel = new GoodsTableViewModel();
            goodsTableViewModel.setName(childCategory.getCategoryName());
            goodsTableViewModel.setOrderNumber(String.valueOf(childCategory.getOrderNumber()));
            goodsTableViewModel.setStatus(childCategory.getStatus().toI18nString());
        }
        return goodsTableViewModel;
    }

    private void sortTreeItemChildren(TreeItem<GoodsTableViewModel> treeItem) {
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
            ErrorMessage.showErrorMessage(root, e.getMessage());
        } finally {
            productForm.hide();
            initCategoriesAndScrollBack();
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
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForModify"));
            return;
        }
        GoodsTableViewModel selected = getGoodsTableSelectedValue();
        GoodsTableViewModel parent = categoriesTable.getSelectionModel().getSelectedItem().getParent().getValue();
        if(selected.isCategory()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForModify"));
            return;
        }
        initProductForm();
        productFormController.setProductViewModel(selected);
        productFormController.setCategory(parent);
        showPopup(productForm, productFormController, root, new Point2D(520, 200));
    }

    private GoodsTableViewModel getGoodsTableSelectedValue() {
        return categoriesTable.getSelectionModel().getSelectedItem().getValue();
    }

    @FXML
    public void onDeleteProduct(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectProductForDelete"));
            return;
        }
        GoodsTableViewModel selected = getGoodsTableSelectedValue();
        if(selected.isCategory()) {
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
        GoodsTableViewModel selected = getGoodsTableSelectedValue();
        if(selected.isProduct()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForModify"));
            return;
        }
        initCategoryForm();
        categoryFormController.setCategory(getGoodsTableSelectedValue());
        showPopup(categoryForm, categoryFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onDeleteCategory(Event event) {
        if(isSelectionNull()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForDelete"));
            return;
        }
        GoodsTableViewModel selected = getGoodsTableSelectedValue();
        if(selected.isProduct()) {
            ErrorMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("ProductForm.SelectCategoryForDelete"));
            return;
        }
        managerService.deleteProductCategory(selected.getName());
        initCategories();
    }

    @FXML
    public void onShowRecipeForm(Event event) {
        if(!isSelectionNull()) {
            GoodsTableViewModel selected = getGoodsTableSelectedValue();
            if (selected.isProduct() && selected.isSellable()) {
                recipeFormController.setSelectedProduct(selected);
            }
        } else {
            recipeFormController.updateComponentsTable();
        }
        showPopup(recipeForm, recipeFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onRefreshRecipes(Event event) {
        recipeFormController.initProducts();
    }

    private boolean isSelectionNull() {
        return categoriesTable.getSelectionModel().getSelectedItem() == null;
    }
}
