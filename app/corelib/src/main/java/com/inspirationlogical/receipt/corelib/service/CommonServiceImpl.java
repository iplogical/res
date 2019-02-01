package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.exception.RootCategoryNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.*;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductStatus.ACTIVE;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductType.*;
import static java.util.stream.Collectors.toList;

//@Singleton
@Service
@Transactional
public class CommonServiceImpl extends AbstractService implements CommonService {

    @Autowired
    private ProductService productService;

    @Autowired
    public CommonServiceImpl(EntityViews entityViews) {
        super(entityViews);
    }

    @Override
    public Product.ProductBuilder productBuilder() {
        return Product.builder();
    }

    @Override
    public ProductCategoryView getRootProductCategory() {
        return entityViews.getCategoryViews().stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(ROOT))
                .findFirst().orElseThrow(RootCategoryNotFoundException::new);
    }


    @Override
    public List<ProductCategoryView> getAggregateCategories() {
        return entityViews.getCategoryViews().stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(AGGREGATE))
                .collect(toList());
    }

    @Override
    public List<ProductCategoryView> getLeafCategories() {
        return entityViews.getCategoryViews().stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(LEAF))
                .collect(toList());
    }

    @Override
    public List<ProductCategoryView> getChildCategories(ProductCategoryView productCategoryView) {
        return entityViews.getCategoryViews().stream()
                .filter(this::isNotRootCategory)
                .filter(categoryView -> isMyChild(productCategoryView, categoryView))
                .collect(toList());
    }

    private boolean isMyChild(ProductCategoryView productCategoryView, ProductCategoryView categoryView) {
        return categoryView.getParent().getCategoryName().equals(productCategoryView.getCategoryName());
    }

    private boolean isNotRootCategory(ProductCategoryView categoryView) {
        return !categoryView.getType().equals(ROOT);
    }

    @Override
    public List<ProductView> getActiveProducts() {
        return entityViews.getProductViews().stream()
                .filter(productView -> productView.getStatus().equals(ACTIVE))
                .collect(toList());
    }

    @Override
    public List<ProductView> getSellableProducts() {
        return getActiveProducts().stream()
                .filter(productView -> !productView.getType().equals(AD_HOC_PRODUCT))
                .filter(productView -> !productView.getType().equals(GAME_FEE_PRODUCT))
                .filter(productView -> !productView.getType().equals(STORABLE))
                .collect(toList());
    }

    @Override
    public List<ProductView> getSellableProducts(ProductCategoryView productCategoryView) {
        return getChildPseudoCategories(productCategoryView).stream()
                .map(ProductCategoryView::getProduct)
                .filter(productView -> !productView.getType().equals(AD_HOC_PRODUCT))
                .filter(productView -> !productView.getType().equals(GAME_FEE_PRODUCT))
                .filter(productView -> !productView.getType().equals(SERVICE_FEE_PRODUCT))
                .filter(productView -> !productView.getType().equals(STORABLE))
                .filter(productView -> productView.getStatus().equals(ACTIVE))
                .collect(toList());
    }

    private List<ProductCategoryView> getChildPseudoCategories(ProductCategoryView productCategoryView) {
        List<ProductCategoryView> childCategories = new ArrayList<>();
        getChildCategoriesRecursively(productCategoryView, childCategories);
        return childCategories.stream()
                .filter(productCategory -> productCategory.getType().equals(PSEUDO))
                .collect(toList());
    }

    private void getChildCategoriesRecursively(ProductCategoryView current, List<ProductCategoryView> traversal) {
        traversal.add(current);
        entityViews.getCategoryViews().stream()
                .filter(categoryView -> isNotRootCategory(categoryView) && isMyChild(current, categoryView))
                .forEach(categoryView -> getChildCategoriesRecursively(categoryView, traversal));
    }

    @Override
    public List<ProductView> getStorableProducts() {
        return productService.getStorableProducts().stream().map(ProductView::new).collect(toList());
    }
}
