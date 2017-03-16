package com.inspirationlogical.receipt.corelib.model.adapter;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.entity.Product;

public class ProductAdapter extends AbstractAdapter<Product> {
    public ProductAdapter(Product adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    public void setPrice(int price) {
        
    }
}
