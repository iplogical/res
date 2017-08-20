package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.exception.RootCategoryNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Product.ProductBuilder;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.utility.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class ProductCategoryAdapter extends AbstractAdapter<ProductCategory>
{

    public static ProductCategoryAdapter getRootCategory() {
        List<ProductCategory> rootCategoryList = GuardedTransaction.runNamedQuery(ProductCategory.GET_CATEGORY_BY_TYPE,
                query -> {query.setParameter("type", ProductCategoryType.ROOT);
                    return query;});
        if(rootCategoryList.isEmpty()) {
            throw new RootCategoryNotFoundException();
        }
        return new ProductCategoryAdapter(rootCategoryList.get(0));
    }

    public static List<ProductCategoryAdapter> getProductCategories() {
        List<ProductCategory> categories = GuardedTransaction.runNamedQuery(ProductCategory.GET_ALL_CATEGORIES);
        return categories.stream()
                .filter(productCategory -> !productCategory.getType().equals(ProductCategoryType.PSEUDO_DELETED))
                .map(ProductCategoryAdapter::new).collect(toList());
    }

    public static List<ProductCategoryAdapter> getCategoriesByType(ProductCategoryType type) {
        List<ProductCategory> aggregates = GuardedTransaction.runNamedQuery(ProductCategory.GET_CATEGORY_BY_TYPE,
                query -> query.setParameter("type", type));
        return aggregates.stream().map(ProductCategoryAdapter::new)
                .collect(toList());
    }

    public static ProductCategoryAdapter getProductCategoryByName(String name) {
        List<ProductCategory> categories = GuardedTransaction.runNamedQuery(ProductCategory.GET_CATEGORY_BY_NAME,
                query -> query.setParameter("name", name));
        if(categories.size() == 1) {
            return new ProductCategoryAdapter(categories.get(0));
        }
        return null;
     }

    public ProductCategoryAdapter(ProductCategory adaptee) {
        super(adaptee);
    }

    public double getDiscount(ReceiptRecordAdapter receiptRecordAdapter) {
        ProductCategory category = adaptee;
        List<PriceModifier> priceModifiers = new ArrayList<>();
        do {
            List<PriceModifier> loopModifiers = getPriceModifiersByOwnerAndDates(category);
            category = category.getParent();
            priceModifiers.addAll(loopModifiers);
        } while(!category.getType().equals(ProductCategoryType.ROOT));

        return priceModifiers.stream()
                .map(PriceModifierAdapter::new)
                .filter(PriceModifierAdapter::isValidNow)
                .map(pm -> pm.getDiscountPercent(receiptRecordAdapter))
                .max(Double::compareTo).orElse(0D);
    }

    private List<PriceModifier> getPriceModifiersByOwnerAndDates(ProductCategory category) {
        return GuardedTransaction.runNamedQuery(PriceModifier.GET_PRICE_MODIFIERS_BY_PRODUCT_AND_DATES,
                query -> {
                    query.setParameter("owner_id", category.getId());
                    query.setParameter("time", now());
                    return query;});
    }

    public ProductCategoryAdapter addChildCategory(String name, ProductCategoryType type) {
        ProductCategory newCategory = buildNewCategory(name, type);
        if(isCategoryNameUsed(newCategory.getName()))
            throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed") + name);
        if(type.equals(ProductCategoryType.AGGREGATE) && parentHasLeafChild()) {
            throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryAlreadyHasLeaf"));
        }
        adaptee.getChildren().add(newCategory);
        newCategory.setParent(adaptee);
        GuardedTransaction.persist(newCategory);
        return new ProductCategoryAdapter(newCategory);
    }

    private ProductCategory buildNewCategory(String name, ProductCategoryType type) {
        return ProductCategory.builder()
                .name(name)
                .type(type)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private static boolean isCategoryNameUsed(String name) {
        return GuardedTransaction.runNamedQuery(ProductCategory.GET_CATEGORY_BY_NAME, query ->
            query.setParameter("name", name)).size() != 0;
    }

    private boolean parentHasLeafChild() {
        return adaptee.getChildren().stream()
                .filter(productCategory -> productCategory.getType().equals(ProductCategoryType.LEAF))
                .collect(Collectors.toList()).size() != 0;
    }

    public static ProductCategoryAdapter updateProductCategory(String newName, String originalName, ProductCategoryType type) {
        if(isCategoryNameUsed(newName)) {
            throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed") + newName);
        }
        ProductCategory originalCategory = getProductCategoryByName(originalName).getAdaptee();
        originalCategory.setName(newName);
        GuardedTransaction.persist(originalCategory);
        return new ProductCategoryAdapter(originalCategory);
    }

    public void deleteProductCategory() {
        GuardedTransaction.run(() -> getAdaptee().setStatus(ProductStatus.DELETED));
    }

    public ProductAdapter addProduct(ProductBuilder builder) {
        Product newProduct = builder.build();
        GuardedTransaction.run(() -> {
            if(isProductNameUsed(newProduct))
                throw new IllegalProductStateException(Resources.CONFIG.getString("ProductNameAlreadyUsed") + newProduct.getLongName());
            ProductCategory pseudo = buildPseudoCategory(newProduct);
            adaptee.getChildren().add(pseudo);
            newProduct.setCategory(pseudo);
            newProduct.setRecipes(new ArrayList<>(Collections.singletonList(buildRecipe(newProduct))));
        });
        return new ProductAdapter(newProduct);
    }

    private boolean isProductNameUsed(Product product) {
        return GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME, query ->
            query.setParameter("longName", product.getLongName())).size() != 0;
    }

    private ProductCategory buildPseudoCategory(Product newProduct) {
        return ProductCategory.builder()
                .name(newProduct.getLongName() + "pseudo")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .parent(adaptee)
                .product(newProduct)
                .build();
    }

    private Recipe buildRecipe(Product newProduct) {
        return Recipe.builder()
                .component(newProduct)
                .quantityMultiplier(1)
                .owner(newProduct)
                .build();
    }
}
