package com.inspirationlogical.receipt.corelib.service;

import static com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter.distinctByKey;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.AGGREGATE;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.LEAF;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.PSEUDO;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.ROOT;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductStatus.ACTIVE;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductType.AD_HOC_PRODUCT;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductType.GAME_FEE_PRODUCT;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductType.STORABLE;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.RootCategoryNotFoundException;
import com.inspirationlogical.receipt.corelib.model.adapter.PriceModifierAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.RecipeAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ProductViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.RecipeView;
import com.inspirationlogical.receipt.corelib.model.view.RecipeViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.StockView;
import com.inspirationlogical.receipt.corelib.model.view.StockViewImpl;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

@Singleton
public class CommonServiceImpl extends AbstractService implements CommonService {

    @Override
    public Product.ProductBuilder productBuilder() {
        return Product.builder();
    }

    @Override
    public PriceModifier.PriceModifierBuilder priceModifierBuilder() {
        return PriceModifier.builder();
    }

    @Override
    public ProductView addProduct(ProductCategoryView parent, Product.ProductBuilder builder) {
        return new ProductViewImpl(getProductCategoryAdapter(parent).addProduct(builder));
    }

    @Override
    public ProductView updateProduct(Long productId, ProductCategoryView parent, Product.ProductBuilder builder) {
        return new ProductViewImpl(ProductAdapter.getProductById(productId).updateProduct(parent.getCategoryName(), builder));
    }

    @Override
    public void deleteProduct(String longName) {
        ProductAdapter.getProductByName(longName).deleteProduct();
    }

    @Override
    public ProductCategoryView addProductCategory(ProductCategoryParams params) {
        return new ProductCategoryViewImpl(getProductCategoryAdapter(params.getParent())
                .addChildCategory(params.getName(), params.getType()));
    }

    @Override
    public ProductCategoryView updateProductCategory(ProductCategoryParams params) {
        return new ProductCategoryViewImpl(ProductCategoryAdapter
                .updateProductCategory(params.getName(), params.getOriginalName(), params.getType()));
    }

    @Override
    public void deleteProductCategory(String name) {
        ProductCategoryAdapter.getProductCategoryByName(name).deleteProductCategory();
    }

    @Override
    public void updateStock(List<StockParams> params, ReceiptType receiptType) {
        TableAdapter.getTablesByType(TableType.getTableType(receiptType)).get(0)
                .updateStock(params, receiptType);
    }

    @Override
    public void addPriceModifier(PriceModifierParams params) {
        PriceModifierAdapter.addPriceModifier(params);
    }

    @Override
    public void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList) {
        ProductAdapter ownerProduct = getProductAdapter(owner);
        ownerProduct.updateRecipes(recipeParamsList);
        ownerProduct.addRecipes(recipeParamsList);
        ownerProduct.deleteRecipes(recipeParamsList);
    }

    @Override
    public ProductCategoryView getRootProductCategory() {
        return categoryViews.stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(ROOT))
                .findFirst().orElseThrow(RootCategoryNotFoundException::new);
    }

    @Override
    public List<ProductCategoryView> getAllCategories() {
        return categoryViews;
    }

    @Override
    public List<ProductCategoryView> getAggregateCategories() {
        return categoryViews.stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(AGGREGATE))
                .collect(toList());
    }

    @Override
    public List<ProductCategoryView> getLeafCategories() {
        return categoryViews.stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(LEAF))
                .collect(toList());
    }

    @Override
    public List<ProductCategoryView> getChildCategories(ProductCategoryView productCategoryView) {
        return categoryViews.stream()
                .filter(categoryView -> {
                    if (!categoryView.getType().equals(ROOT)) {
                        return categoryView.getParent().getCategoryName().equals(productCategoryView.getCategoryName());
                    }
                    return false;
                })
                .collect(toList());
    }

    @Override
    public void getChildCategoriesRecursively(ProductCategoryView current, List<ProductCategoryView> traversal) {
        traversal.add(current);
        categoryViews.stream()
                .filter(categoryView -> {
                    if (!categoryView.getType().equals(ROOT)) {
                        return categoryView.getParent().getCategoryName().equals(current.getCategoryName());
                    }
                    return false;
                })
                .forEach(categoryView -> getChildCategoriesRecursively(categoryView, traversal));
    }

    @Override
    public List<ProductCategoryView> getChildPseudoCategories(ProductCategoryView productCategoryView) {
        List<ProductCategoryView> childCategories = new ArrayList<>();
        getChildCategoriesRecursively(productCategoryView, childCategories);
        return childCategories.stream()
                .filter(productCategory -> productCategory.getType().equals(PSEUDO))
                .collect(toList());
    }

    @Override
    public List<ProductView> getActiveProducts() {
        return productViews.stream()
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
                .filter(productView -> !productView.getType().equals(STORABLE))
                .collect(toList());
    }

    @Override
    public List<ProductView> getStorableProducts() {
        List<ProductView> productsAsRecipe = getActiveProducts().stream()
                .flatMap(productView -> productView.getRecipes().stream())
                .map(RecipeView::getComponent)
                .filter(distinctByKey(ProductView::getLongName))
                .collect(toList());
        List<ProductView> storableProducts = getActiveProducts().stream()
                .filter(productView -> productView.getType().equals(STORABLE))
                .collect(toList());
        productsAsRecipe.addAll(storableProducts);
        return productsAsRecipe.stream()
                .filter(distinctByKey(ProductView::getLongName))
                .collect(toList());
    }

    @Override
    public List<StockView> getStockItems() {
        return createViewsFromAdapters(StockAdapter.getItems(), StockViewImpl::new);
    }

    @Override
    public List<PriceModifierView> getPriceModifiers() {
        return createViewsFromAdapters(PriceModifierAdapter.getPriceModifiers(), PriceModifierViewImpl::new);
    }

    @Override
    public List<RecipeView> getRecipeComponents(ProductView product) {
        return createViewsFromAdapters(RecipeAdapter.getRecipesOfProduct(getProductAdapter(product)), RecipeViewImpl::new);
    }

    @Override
    public List<ReceiptView> getReceipts() {
        return ReceiptAdapter.getReceipts()
                .stream()
                .map(ReceiptViewImpl::new)
                .collect(toList());
    }
}
