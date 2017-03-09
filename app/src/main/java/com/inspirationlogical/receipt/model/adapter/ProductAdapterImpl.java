package com.inspirationlogical.receipt.model.adapter;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.Product;

public class ProductAdapterImpl extends AbstractAdapterImpl<Product>
    implements ProductAdapter {

    public ProductAdapterImpl(Product adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    @Override
    public void setPrice(int price) {
        
    }

}
