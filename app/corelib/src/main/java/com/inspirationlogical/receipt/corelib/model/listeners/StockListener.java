package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;

import static com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider.getEntityManager;

/**
 * Created by Bálint on 2017.04.04..
 */
public class StockListener implements ReceiptAdapter.Listener {

    @Override
    public void onOpen(ReceiptAdapter receipt) {
        System.out.println("A receipt was opened.");
    }

    @Override
    public void onClose(ReceiptAdapter receipt) {
        // TODO: test this.
        receipt.getSoldProducts().forEach(
            receiptRecordAdapter -> {
                if(receiptRecordAdapter.isComplexProduct()) {
                    updateStockForRecipeElements(receipt.getAdaptee().getType(), receiptRecordAdapter);
                } else {
                    updateStockForProduct(receiptRecordAdapter.getProductAdapter(),
                            receiptRecordAdapter.getAdaptee().getSoldQuantity(),
                            receipt.getAdaptee().getType());
                }
            }
        );
    }

    private void updateStockForProduct(ProductAdapter productAdapter, double soldQuantity, ReceiptType type) {
        StockAdapter stockAdapter =
                StockAdapter.getLatestItemByProduct(productAdapter);
        stockAdapter.updateStockAdapter(soldQuantity, type);
    }

    private void updateStockForRecipeElements(ReceiptType type, ReceiptRecordAdapter receiptRecordAdapter) {
        receiptRecordAdapter.getAdaptee().getProduct().getRecipe().forEach(recipe -> {
            updateStockForProduct(new ProductAdapter(recipe.getElement()), receiptRecordAdapter.getAdaptee().getSoldQuantity() * recipe.getQuantityMultiplier(), type);
        });
    }
}
