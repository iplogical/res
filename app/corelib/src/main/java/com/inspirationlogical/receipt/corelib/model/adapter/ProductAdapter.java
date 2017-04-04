package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.List;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.exception.AdHocProductNotFoundException;
import com.inspirationlogical.receipt.corelib.exception.GameFeeProductNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ProductAdapter extends AbstractAdapter<Product> {

    public static ProductAdapter getAdHocProduct(EntityManager manager) {
                List<Product> adHocProductList = manager.createNamedQuery(Product.GET_PRODUCT_BY_TYPE)
                .setParameter("type", ProductType.AD_HOC_PRODUCT)
                .getResultList();
        if (adHocProductList.isEmpty()) {
            throw new AdHocProductNotFoundException();
        }
        return new ProductAdapter(adHocProductList.get(0));
    }

    public static ProductAdapter getGameFeeProduct(EntityManager entityManager) {
        List<Product> gameFeeProductList = GuardedTransaction.RunNamedQuery(Product.GET_PRODUCT_BY_TYPE,
                query -> query.setParameter("type", ProductType.GAME_FEE_PRODUCT));
        if(gameFeeProductList.isEmpty()) {
            throw new GameFeeProductNotFoundException();
        }
        return new ProductAdapter(gameFeeProductList.get(0));
    }

    public ProductAdapter(Product adaptee) {
        super(adaptee);
    }


    public ProductCategoryAdapter getCategoryAdapter() {
        return new ProductCategoryAdapter(adaptee.getCategory());
    }
}
