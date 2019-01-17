package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

public class SearchProduct implements Search {

    private List<ProductView> allProducts;

    public SearchProduct(List<ProductView> allProducts) {
        this.allProducts = allProducts;
    }

    @Override
    public List<ProductView> search(String pattern) {
        if(isNumeric(pattern)) {
            return allProducts.stream()
                    .filter(rapidCodeEquals(pattern))
                    .collect(Collectors.toList());
        }
        List<ProductView> matchingProducts = allProducts.stream()
                .filter(startsWithPattern(pattern))
                .sorted(Comparator.comparing(ProductView::getShortName))
                .collect(Collectors.toList());
        List<ProductView> otherProducts = allProducts.stream()
                .filter(containsPattern(pattern))
                .sorted(Comparator.comparing(ProductView::getShortName))
                .collect(Collectors.toList());
        matchingProducts.addAll(otherProducts);
        return matchingProducts;
    }

    private Predicate<ProductView> rapidCodeEquals(String pattern) {
        return productView -> productView.getRapidCode() == Integer.parseInt(pattern);
    }

    private Predicate<ProductView> startsWithPattern(String pattern) {
        return productView -> startsWithIgnoreCase(productView.getShortName(), pattern);
    }

    private Predicate<ProductView> containsPattern(String pattern) {
        return productView -> !startsWithIgnoreCase(productView.getShortName(), pattern) &&
                containsIgnoreCase(productView.getShortName(), pattern);
    }
}
