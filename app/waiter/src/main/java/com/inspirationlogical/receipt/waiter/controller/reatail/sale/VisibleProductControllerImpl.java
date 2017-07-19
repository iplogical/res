package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class VisibleProductControllerImpl implements VisibleProductController {

    private final int GRID_SIZE = 4;

    @Setter
    private ViewLoader viewLoader;

    @Setter
    private SaleController saleController;

    private CommonService commonService;

    private Search productSearcher;

    private ProductCategoryView rootCategory;

    @Setter
    private GridPane categoriesGrid;

    @Setter
    private GridPane subCategoriesGrid;

    @Setter
    private GridPane productsGrid;

    @Getter
    private ProductCategoryView selectedCategory;

    @Getter
    private List<ProductCategoryView> selectedLevelCategories;

    @Getter
    private List<ProductCategoryView> selectedChildrenCategories;

    @Getter
    private List<ProductView> searchedProducts;

    @Getter
    private List<ProductView> visibleProducts;

    private List<SaleElementController> elementControllers;

    @Inject
    public VisibleProductControllerImpl(CommonService commonService) {
        this.commonService = commonService;
        this.elementControllers = new ArrayList<>();
        initializeCategories();
        initializeProductSearcher();
    }

    private void initializeCategories() {
        rootCategory = commonService.getRootProductCategory();
        List<ProductCategoryView> childCategories = getChildCategoriesWithSellableProduct(rootCategory);
        childCategories.sort(Comparator.comparing(ProductCategoryView::getOrderNumber));
        selectedCategory = childCategories.get(0);
    }

    private void initializeProductSearcher() {
        productSearcher = new SearchProduct(commonService.getSellableProducts());
    }

    private List<ProductCategoryView> getChildCategoriesWithSellableProduct(ProductCategoryView categoryView) {
        return commonService.getChildCategories(categoryView).stream()
                .filter(productCategoryView -> !commonService.getSellableProducts(productCategoryView).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void selectCategory(SaleElementController saleElementController) {
        selectedCategory = (ProductCategoryView) saleElementController.getView();
        updateCategories();
    }

    @Override
    public void updateCategories() {
        if(selectedCategoryIsNotLeaf(selectedCategory)) {
            selectedLevelCategories = getChildCategoriesWithSellableProduct(selectedCategory.getParent());
            selectedChildrenCategories = commonService.getChildCategories(selectedCategory);
        }
        visibleProducts = commonService.getSellableProducts(selectedCategory);
        redrawCategoriesAndProducts();
    }

    private boolean selectedCategoryIsNotLeaf(ProductCategoryView selectedCategory) {
        return !ProductCategoryType.isLeaf(selectedCategory.getType());
    }

    private void redrawCategoriesAndProducts() {
        elementControllers.clear();
        redrawTopCategories();
        redrawSubCategories();
        setSelectedCategory();
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
                .forEach(controller -> controller.select());
    }

    private <T extends AbstractView> void drawListOfElements(List<T> elements, GridPane grid) {
        elements.sort(Comparator.comparing(AbstractView::getOrderNumber));
        for(int i = 0; i < Math.min(elements.size(), 32); i++) {
            drawElement(elements.get(i), grid, i);
        }
    }

    private <T extends AbstractView> void drawElement(T elementView, GridPane grid, int index) {
        SaleElementController elementController = null;

        if(elementView instanceof ProductView) {
            elementController = new SaleProductControllerImpl(saleController);
        } else if (elementView instanceof ProductCategoryView) {
            elementController = new SaleCategoryControllerImpl(saleController);
        }
        elementController.setView(elementView);
        elementControllers.add(elementController);
        viewLoader.loadView(elementController);
        grid.add(elementController.getRootNode(), index % GRID_SIZE, index / GRID_SIZE);
    }

    private void drawBackButton(GridPane categoriesGrid) {
        final int BUTTON_POSITION = GRID_SIZE - 1;
        SaleElementController elementController = new SaleProductControllerImpl(saleController) {

            @Override
            public void onElementClicked(MouseEvent event) {
                upWithCategories();
                redrawCategoriesAndProducts();
            }
        };
        elementController.setView((ProductCategoryView) () -> "Vissza");
        viewLoader.loadView(elementController);
        categoriesGrid.add(elementController.getRootNode(), BUTTON_POSITION, BUTTON_POSITION);
    }

    private void upWithCategories() {
        if(isTopLevelCategorySelected()) {
            return;
        }
        selectedCategory = selectedCategory.getParent();
        updateCategories();
    }

    private boolean isTopLevelCategorySelected() {
        return ProductCategoryType.isRoot(selectedCategory.getParent().getType());
    }

    @Override
    public void search(String searchText) {
        productsGrid.getChildren().clear();
        if (!searchText.isEmpty()) {
            searchedProducts = productSearcher.search(searchText);
            drawListOfElements(searchedProducts, productsGrid);
        } else {
            drawListOfElements(visibleProducts, productsGrid);
        }
    }
}
