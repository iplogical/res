package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.service.price_modifier.PriceModifierService;
import com.inspirationlogical.receipt.corelib.service.product.ProductService;
import com.inspirationlogical.receipt.corelib.service.product_category.ProductCategoryService;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import com.inspirationlogical.receipt.corelib.service.receipt_record.ReceiptRecordService;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ManagerServiceImpl extends AbstractService implements ManagerService {

    final private static Logger logger = LoggerFactory.getLogger(ManagerServiceImpl.class);

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private ReceiptRecordService receiptRecordService;

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private PriceModifierService priceModifierService;

    @Autowired
    ManagerServiceImpl(EntityViews entityViews) {
        super(entityViews);
    }

    @Override
    public PriceModifier.PriceModifierBuilder priceModifierBuilder() {
        return PriceModifier.builder();
    }

    @Override
    public void addProduct(ProductCategoryView parent, Product.ProductBuilder builder) {
        productService.addProduct(parent, builder);
        entityViews.initEntityViews();
    }

    @Override
    public void updateProduct(Long productId, ProductCategoryView parent, Product.ProductBuilder builder) {
        productService.updateProduct(productId, parent.getCategoryName(), builder);
    }

    @Override
    public void deleteProduct(String longName) {
        productService.deleteProduct(longName);
    }

    @Override
    public void addProductCategory(ProductCategoryParams params) {
        productCategoryService.addProductCategory(params);
        entityViews.initEntityViews();
    }

    @Override
    public void updateProductCategory(ProductCategoryParams params) {
        productCategoryService.updateProductCategory(params);
    }

    @Override
    public void deleteProductCategory(String name) {
        productCategoryService.deleteProductCategory(name);
    }

    @Override
    public void updateStock(List<StockParams> params, ReceiptType receiptType, StockListener.StockUpdateListener listener) {
        receiptService.updateStock(params, receiptType, listener);
    }

    @Override
    public void addPriceModifier(PriceModifierParams params) {
        priceModifierService.addPriceModifier(params);
    }

    @Override
    public void updatePriceModifier(PriceModifierParams params) {
        priceModifierService.updatePriceModifier(params);
    }

    @Override
    public void deletePriceModifier(PriceModifierParams params) {
        priceModifierService.deletePriceModifier(params);
    }

    @Override
    public void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList) {
        productService.updateRecipe(owner, recipeParamsList);
    }

    @Override
    public List<StockView> getStockItems() {
        return stockService.getItems();
    }

    @Override
    public List<PriceModifierView> getPriceModifiers() {
        return priceModifierService.getPriceModifiers();
    }

    @Override
    public List<RecipeView> getRecipeComponents(ProductView productView) {
        return productService.getRecipeComponents(productView);
    }

    @Override
    public List<ReceiptView> getReceipts(LocalDate startDate, LocalDate endDate) {
        return ReceiptAdapterBase.getReceipts(startDate, endDate)
                .stream()
                .map(ReceiptViewImpl::new)
                .collect(toList());
    }

    @Override
    public ReceiptRecordView decreaseReceiptRecord(ReceiptRecordView receiptRecordView, double quantity) {
        logger.info("A receipt record was decreased by the manager: " + receiptRecordView);
        return receiptRecordService.decreaseReceiptRecord(receiptRecordView, quantity);
    }
}
