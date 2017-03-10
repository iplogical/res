package com.inspirationlogical.receipt.model.adapter;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.Product;

public class ProductAdapter extends AbstractAdapter<Product> {
    public ProductAdapter(Product adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    public void setPrice(int price) {
        
    }
}
