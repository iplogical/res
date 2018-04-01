package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ProductViewImpl;
import com.inspirationlogical.receipt.corelib.repository.ProductCategoryRepository;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityViewsImpl implements EntityViews {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    private List<ProductView> productViews;

    private List<ProductCategoryView> categoryViews;

    @Override
    public void initEntityViews() {
        productViews = productRepository.findAllByStatus(ProductStatus.ACTIVE).stream().map(ProductViewImpl::new).collect(Collectors.toList());
        List<ProductCategory> categories = productCategoryRepository.findAllByStatus(ProductStatus.ACTIVE);
        categoryViews = categories.stream().map(ProductCategoryViewImpl::new).collect(Collectors.toList());
    }

    @Override
    public List<ProductView> getProductViews() {
        if(productViews == null) {
            initEntityViews();
        }
        return productViews;
    }

    @Override
    public List<ProductCategoryView> getCategoryViews() {
        if(categoryViews == null) {
            initEntityViews();
        }
        return categoryViews;
    }
}
