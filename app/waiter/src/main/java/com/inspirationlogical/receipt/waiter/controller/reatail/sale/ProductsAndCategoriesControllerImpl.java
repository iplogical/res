package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductsAndCategoriesControllerImpl implements ProductsAndCategoriesController {

    private final int GRID_SIZE = 4;

//    @Setter private ViewLoader viewLoader;
    @Setter private SaleController saleController;
    private CommonService commonService;

    private Search productSearcher;

    private ProductCategoryView rootCategory;

    @Setter private GridPane categoriesGrid;
    @Setter private GridPane subCategoriesGrid;
    @Setter private GridPane productsGrid;

    private ProductCategoryView selectedCategory;
    private ProductCategoryView initialSelectedCategory;
    private List<ProductCategoryView> selectedLevelCategories;
    private List<ProductCategoryView> selectedChildrenCategories;

    @Getter private List<ProductView> searchedProducts;
    private List<ProductView> visibleProducts;

    private List<ElementController> elementControllers;

    @Autowired
    public ProductsAndCategoriesControllerImpl(CommonService commonService) {
        this.commonService = commonService;
        this.elementControllers = new ArrayList<>();
        initializeCategories();
        initializeProductSearcher();
    }

    private void initializeCategories() {
        rootCategory = commonService.getRootProductCategory();
        List<ProductCategoryView> childCategories = getChildCategoriesWithSellableProduct(rootCategory);
        childCategories.sort(Comparator.comparing(ProductCategoryView::getOrderNumber));
        initialSelectedCategory = selectedCategory = childCategories.get(0);
    }

    private List<ProductCategoryView> getChildCategoriesWithSellableProduct(ProductCategoryView categoryView) {
        return commonService.getChildCategories(categoryView).stream()
                .filter(productCategoryView -> !commonService.getSellableProducts(productCategoryView).isEmpty())
                .collect(Collectors.toList());
    }

    private void initializeProductSearcher() {
        productSearcher = new SearchProduct(commonService.getSellableProducts());
    }

    @Override
    public void selectCategory(ProductCategoryView selected) {
        selectedCategory = selected;
        updateCategoriesAndProducts();
    }

    @Override
    public void initCategoriesAndProducts() {
        selectedCategory = initialSelectedCategory;
        visibleProducts = new ArrayList<>();
        updateAndRedrawSelectedCategoriesAndProducts();
    }

    @Override
    public void updateCategoriesAndProducts() {
        visibleProducts = commonService.getSellableProducts(selectedCategory);
        updateAndRedrawSelectedCategoriesAndProducts();
    }

    private void updateAndRedrawSelectedCategoriesAndProducts() {
        updateSelectedCategories();
        redrawCategoriesAndProducts();
        setSelectedCategory();
    }

    private void updateSelectedCategories() {
        if(selectedCategoryIsAggregate(selectedCategory)) {
            selectedLevelCategories = getChildCategoriesWithSellableProduct(selectedCategory.getParent());
            selectedChildrenCategories = commonService.getChildCategories(selectedCategory);
        } else if(selectedCategoryIsSameLevel(selectedCategory)){
            selectedChildrenCategories.clear();
        }
    }

    private boolean selectedCategoryIsAggregate(ProductCategoryView selectedCategory) {
        return ProductCategoryType.isAggregate(selectedCategory.getType());
    }

    private boolean selectedCategoryIsSameLevel(ProductCategoryView selectedCategory) {
        return selectedLevelCategories.stream().filter(category ->
                category.getCategoryName().equals(selectedCategory.getCategoryName()))
                .count() == 1;
    }

    private void redrawCategoriesAndProducts() {
        elementControllers.clear();
        redrawTopCategories();
        redrawSubCategories();
        redrawProducts();
    }

    private void redrawTopCategories() {
        categoriesGrid.getChildren().clear();
        drawListOfElements(selectedLevelCategories, categoriesGrid);
        drawBackButton(categoriesGrid);
    }

    private void redrawSubCategories() {
        subCategoriesGrid.getChildren().clear();
        drawListOfElements(selectedChildrenCategories, subCategoriesGrid);
    }

    private void redrawProducts() {
        productsGrid.getChildren().clear();
        drawListOfElements(visibleProducts, productsGrid);
    }

    private void setSelectedCategory() {
        elementControllers.stream()
                .filter(controller -> controller.getView().getName().equals(selectedCategory.getName()))
                .forEach(ElementController::select);
    }

    private <T extends AbstractView> void drawListOfElements(List<T> elements, GridPane grid) {
        elements.sort(Comparator.comparing(AbstractView::getOrderNumber));
        draw32Elements(elements, grid);
    }

    private <T extends AbstractView> void draw32Elements(List<T> elements, GridPane grid) {
        for(int i = 0; i < Math.min(elements.size(), 32); i++) {
            drawElement(elements.get(i), grid, i);
        }
    }

    private <T extends AbstractView> void drawElement(T elementView, GridPane grid, int index) {
        ElementController elementController = null;

        if(elementView instanceof ProductView) {
            elementController = new ProductControllerImpl(saleController);
        } else if (elementView instanceof ProductCategoryView) {
            elementController = new CategoryControllerImpl(saleController, this);
        }
        elementController.setView(elementView);
        elementControllers.add(elementController);
//        viewLoader.loadView(elementController);
        // TODO_REFACTOR: add buttons somehow.
//        WaiterApp.showView();
        grid.add(elementController.getRootNode(), index % GRID_SIZE, index / GRID_SIZE);
    }

    private void drawBackButton(GridPane categoriesGrid) {
        final int BUTTON_POSITION = GRID_SIZE - 1;
        ElementController elementController = new ProductControllerImpl(saleController) {

            @Override
            public void onElementClicked(MouseEvent event) {
                upWithCategories();
                redrawCategoriesAndProducts();
                setSelectedCategory();
            }
        };
        elementController.setView((ProductCategoryView) () -> WaiterResources.WAITER.getString("SaleView.BackButton"));
        // TODO_REFACTOR: add buttons somehow.
//        viewLoader.loadView(elementController);
        categoriesGrid.add(elementController.getRootNode(), BUTTON_POSITION, BUTTON_POSITION);
    }

    private void upWithCategories() {
        if(isSubLevelCategorySelected()) {
            selectedCategory = selectedCategory.getParent();
            updateCategoriesAndProducts();
        }
    }

    private boolean isSubLevelCategorySelected() {
        return !ProductCategoryType.isRoot(selectedCategory.getParent().getType());
    }

    @Override
    public void search(String searchText) {
        productsGrid.getChildren().clear();
        if (searchText.isEmpty()) {
            drawListOfElements(visibleProducts, productsGrid);
        } else {
            searchedProducts = productSearcher.search(searchText);
            draw32Elements(searchedProducts, productsGrid);
        }
    }
}
