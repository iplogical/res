package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.Product;

public interface ProductAdapter extends AbstractAdapter<Product> {

    void setPrice(int price);
}
