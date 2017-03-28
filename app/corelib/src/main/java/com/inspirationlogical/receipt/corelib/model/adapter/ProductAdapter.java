package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.AdHocProductNotFoundException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;

import javax.persistence.EntityManager;
import java.util.List;

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

    public ProductAdapter(Product adaptee) {
        super(adaptee);
    }

    public void setPrice(int price) {
        
    }
}