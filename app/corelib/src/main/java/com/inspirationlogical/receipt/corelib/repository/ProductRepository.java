package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByType(ProductType type);

    List<Product> findAllByStatus(ProductStatus status);

    Product findByLongName(String longName);
}
