package com.inspirationlogical.receipt.corelib.service.product_category;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    public PriceModifier getPriceModifier(ProductCategory category, ReceiptRecord receiptRecord) {
        List<PriceModifier> priceModifiers = findAllPriceModifiers(category);
        return priceModifiers.stream()
                .filter(pm -> isValidNow(pm, LocalDateTime.now()))
                .max(Comparator.comparing(PriceModifier::getDiscountPercent)).orElse(null);
    }

    private List<PriceModifier> findAllPriceModifiers(ProductCategory category) {
        List<PriceModifier> priceModifiers = new ArrayList<>();
        do {
            List<PriceModifier> loopModifiers = priceModifierRepository.getPriceModifierByProductAndDates(category.getId(), now());
            category = category.getParent();
            priceModifiers.addAll(loopModifiers);
        } while(!category.getType().equals(ProductCategoryType.ROOT));
        return priceModifiers;
    }

    private boolean isValidNow(PriceModifier priceModifier, LocalDateTime now) {
        if(priceModifier.getRepeatPeriod().equals(PriceModifierRepeatPeriod.NO_REPETITION)) {
            return true;
        } else if(priceModifier.getRepeatPeriod().equals(PriceModifierRepeatPeriod.DAILY)) {
            return isTimeMatches(priceModifier, now.toLocalTime());
        } else if(priceModifier.getRepeatPeriod().equals(PriceModifierRepeatPeriod.WEEKLY)) {
            return isDayOfWeekMatches(priceModifier, now);
        }
        return false;
    }

    private boolean isTimeMatches(PriceModifier priceModifier, LocalTime now) {
        return now.plusMinutes(1).isAfter(priceModifier.getStartTime()) && now.minusMinutes(1).isBefore(priceModifier.getEndTime());
    }

    private boolean isDayOfWeekMatches(PriceModifier priceModifier, LocalDateTime now) {
        boolean isToday = now.getDayOfWeek().equals(priceModifier.getDayOfWeek());
        boolean isYesterday = now.minusDays(1).getDayOfWeek().equals(priceModifier.getDayOfWeek());
        boolean isNight = now.toLocalTime().isBefore(LocalTime.of(4,0));
        boolean isDay = now.toLocalTime().isAfter(LocalTime.of(4, 0));
        return (isToday && isDay) || (isYesterday && isNight);
    }
}
