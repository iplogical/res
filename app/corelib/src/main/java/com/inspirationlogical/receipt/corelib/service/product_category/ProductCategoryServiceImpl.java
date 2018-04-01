package com.inspirationlogical.receipt.corelib.service.product_category;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.repository.PriceModifierRepository;
import com.inspirationlogical.receipt.corelib.repository.ProductCategoryRepository;
import com.inspirationlogical.receipt.corelib.service.price_modifier.PriceModifierService;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private PriceModifierService priceModifierService;

    @Autowired
    private PriceModifierRepository priceModifierRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public void addProductCategory(ProductCategoryParams params) {
        ProductCategory parentCategory = productCategoryRepository.getOne(params.getParent().getId());
        ProductCategory newCategory = buildNewCategory(params);
        if(isCategoryNameUsed(params))
            throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed") + params.getName());
        parentCategory.getChildren().add(newCategory);
        newCategory.setParent(parentCategory);
        productCategoryRepository.save(newCategory);
    }

    private ProductCategory buildNewCategory(ProductCategoryParams params) {
        return ProductCategory.builder()
                .name(params.getName())
                .type(params.getType())
                .status(params.getStatus())
                .orderNumber(params.getOrderNumber())
                .build();
    }

    private boolean isCategoryNameUsed(ProductCategoryParams params) {
        if(params.getName().equals(params.getOriginalName())) {
            return false;
        }
        return productCategoryRepository.findByName(params.getName()) != null;
    }

    @Override
    public void updateProductCategory(ProductCategoryParams params) {
        ProductCategory category = productCategoryRepository.findByName(params.getOriginalName());
        if(isCategoryNameUsed(params)) {
            throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed") + params.getName());
        }
        category.setName(params.getName());
        category.setOrderNumber(params.getOrderNumber());
        setStatusOfCategorySubTree(category, params.getStatus());
        productCategoryRepository.save(category);
    }

    private void setStatusOfCategorySubTree(ProductCategory category, ProductStatus status) {
        category.setStatus(status);
        category.getChildren().forEach(childCategory -> setStatusRecursively(childCategory, status));
    }

    private void setStatusRecursively(ProductCategory productCategory, ProductStatus status) {
        productCategory.setStatus(status);
        if(productCategory.getProduct() != null) {
            productCategory.getProduct().setStatus(status);
        } else {
            productCategory.getChildren().forEach(childCategory -> setStatusRecursively(childCategory, status));
        }
    }

    @Override
    public void deleteProductCategory(String name) {
        ProductCategory category = productCategoryRepository.findByName(name);
        setStatusOfCategorySubTree(category, ProductStatus.DELETED);
        productCategoryRepository.save(category);
    }

    @Override
    public double getDiscount(ProductCategory category, ReceiptRecord receiptRecord) {
        List<PriceModifier> priceModifiers = new ArrayList<>();
        do {
            List<PriceModifier> loopModifiers = priceModifierRepository.getPriceModifierByProductAndDates(category.getId(), now());
            category = category.getParent();
            priceModifiers.addAll(loopModifiers);
        } while(!category.getType().equals(ProductCategoryType.ROOT));

        return priceModifiers.stream()
                .filter(priceModifier -> priceModifierService.isValidNow(priceModifier, LocalDateTime.now()))
                .map(pm -> priceModifierService.getDiscountPercent(pm, receiptRecord))
                .max(Double::compareTo)
                .orElse(0D);
    }
}
