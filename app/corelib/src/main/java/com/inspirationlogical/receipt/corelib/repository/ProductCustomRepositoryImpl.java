package com.inspirationlogical.receipt.corelib.repository;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getStorableProducts() {
        return productRepository.findAllByStatus(ProductStatus.ACTIVE).stream()
                .flatMap(product -> product.getRecipes().stream())
                .map(Recipe::getComponent)
                .filter(distinctByKey(Product::getLongName))
                .collect(toList());
    }

    private  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
