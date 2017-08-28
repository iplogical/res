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
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void addChildCategory(ProductCategoryParams params) {
        ProductCategory newCategory = buildNewCategory(params);
        if(isCategoryNameUsed(params))
            throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed") + params.getName());
        adaptee.getChildren().add(newCategory);
        newCategory.setParent(adaptee);
        GuardedTransaction.persist(newCategory);
    }

    private ProductCategory buildNewCategory(ProductCategoryParams params) {
        return ProductCategory.builder()
                .name(params.getName())
                .type(params.getType())
                .status(params.getStatus())
                .orderNumber(params.getOrderNumber())
                .build();
    }

    private static boolean isCategoryNameUsed(ProductCategoryParams params) {
        if(params.getName().equals(params.getOriginalName())) return false;
        return GuardedTransaction.runNamedQuery(ProductCategory.GET_CATEGORY_BY_NAME, query ->
            query.setParameter("name", params.getName())).size() != 0;
    }

    public void updateProductCategory(ProductCategoryParams params) {
        if(isCategoryNameUsed(params)) {
            throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed") + params.getName());
        }
        adaptee.setName(params.getName());
        adaptee.setOrderNumber(params.getOrderNumber());
        setStatusOfCategorySubTree(params.getStatus());
        GuardedTransaction.persist(adaptee);
    }

    private void setStatusOfCategorySubTree(ProductStatus status) {
        adaptee.setStatus(status);
        adaptee.getChildren().forEach(childCategory -> setStatusRecursively(childCategory, status));
    }

    private void setStatusRecursively(ProductCategory productCategory, ProductStatus status) {
        productCategory.setStatus(status);
        if(productCategory.getProduct() != null) {
            productCategory.getProduct().setStatus(status);
        } else {
            productCategory.getChildren().forEach(childCategory -> setStatusRecursively(childCategory, status));
        }
    }

    public void deleteProductCategory() {
        GuardedTransaction.run(() -> {
            setStatusOfCategorySubTree(ProductStatus.DELETED);
        });
    }

    public void addProduct(ProductBuilder builder) {
        Product newProduct = builder.build();
        if(isProductNameUsed(newProduct))
            throw new IllegalProductStateException(Resources.CONFIG.getString("ProductNameAlreadyUsed") + newProduct.getLongName());
        ProductCategory pseudo = buildPseudoCategory(newProduct);
        bindProductToPseudo(newProduct, pseudo);
        addDefaultRecipe(newProduct);
        GuardedTransaction.persist(newProduct);
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

    private void bindProductToPseudo(Product newProduct, ProductCategory pseudo) {
        adaptee.getChildren().add(pseudo);
        newProduct.setCategory(pseudo);
    }

    private void addDefaultRecipe(Product newProduct) {
        newProduct.setRecipes(new ArrayList<>(Collections.singletonList(buildRecipe(newProduct))));
    }

    private Recipe buildRecipe(Product newProduct) {
        return Recipe.builder()
                .component(newProduct)
                .quantityMultiplier(1)
                .owner(newProduct)
                .build();
    }
}
