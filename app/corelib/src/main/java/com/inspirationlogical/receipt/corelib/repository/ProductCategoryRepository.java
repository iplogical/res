package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    ProductCategory findByName(String name);

    List<ProductCategory> findAllByStatus(ProductStatus status);
}
