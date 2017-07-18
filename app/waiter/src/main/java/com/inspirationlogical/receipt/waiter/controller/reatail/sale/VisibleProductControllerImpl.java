package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import lombok.Getter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VisibleProductControllerImpl implements VisibleProductController {

    private CommonService commonService;

    private ProductCategoryView rootCategory;

    @Getter
    private ProductCategoryView selectedCategory;

    @Getter
    private List<ProductCategoryView> selectedLevelCategories;

    @Getter
    private List<ProductCategoryView> selectedChildrenCategories;

    @Getter
    private List<ProductView> visibleProducts;

    private List<ProductView> searchedProducts;

    private List<SaleElementController> elementControllers;

    @Inject
    public VisibleProductControllerImpl(CommonService commonService) {
        this.commonService = commonService;
        this.elementControllers = new ArrayList<>();
        initializeCategories();
    }

    private void initializeCategories() {
        rootCategory = commonService.getRootProductCategory();
        List<ProductCategoryView> childCategories = getChildCategoriesWithSellableProduct(rootCategory);
        childCategories.sort(Comparator.comparing(ProductCategoryView::getOrderNumber));
        selectedCategory = childCategories.get(0);
    }

    private List<ProductCategoryView> getChildCategoriesWithSellableProduct(ProductCategoryView categoryView) {
        return commonService.getChildCategories(categoryView).stream()
                .filter(productCategoryView -> !commonService.getSellableProducts(productCategoryView).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void updateCategories() {
        if(selectedCategoryIsNotLeaf(selectedCategory)) {
            selectedLevelCategories = getChildCategoriesWithSellableProduct(selectedCategory.getParent());
            selectedChildrenCategories = commonService.getChildCategories(selectedCategory);
        }
        visibleProducts = commonService.getSellableProducts(selectedCategory);
    }

    @Override
    public void upWithCategories() {
        if(ProductCategoryType.isRoot(selectedCategory.getParent().getType())) {
            return;
        }
        selectedCategory = selectedCategory.getParent();
        updateCategories();
    }

    @Override
    public void selectCategory(SaleElementController saleElementController) {
        selectedCategory = (ProductCategoryView) saleElementController.getView();
        updateCategories();
    }

    private boolean selectedCategoryIsNotLeaf(ProductCategoryView selectedCategory) {
        return !ProductCategoryType.isLeaf(selectedCategory.getType());
    }
}
