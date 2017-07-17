package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

/**
 * Created by TheDagi on 2017. 07. 17..
 */
public class SearchProduct implements Search {

    private List<ProductView> allProducts;

    public SearchProduct(List<ProductView> allProducts) {
        this.allProducts = allProducts;
    }

    @Override
    public List<ProductView> search(String pattern) {
        try {
            int rapidCode = Integer.parseInt(pattern);
            return allProducts.stream()
                    .filter(productView -> productView.getRapidCode() == rapidCode)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return allProducts.stream()
                    .filter(containsPattern(pattern))
                    .collect(Collectors.toList());
        }
    }

    private static <T extends AbstractView> Predicate<T> containsPattern(String pattern) {
        return productView -> containsIgnoreCase(productView.getName(), pattern);
    }
}
