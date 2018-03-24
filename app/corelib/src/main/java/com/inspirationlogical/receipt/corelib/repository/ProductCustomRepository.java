package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.Product;

import java.util.List;

public interface ProductCustomRepository {

    List<Product> getStorableProducts();
}
