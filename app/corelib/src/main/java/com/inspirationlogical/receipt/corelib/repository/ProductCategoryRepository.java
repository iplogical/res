package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    ProductCategory findByName(String name);
}
