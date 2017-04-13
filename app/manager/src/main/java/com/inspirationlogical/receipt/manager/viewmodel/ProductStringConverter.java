package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import static java.util.stream.Collectors.toList;

/**
 * Created by TheDagi on 2017. 04. 12..
 */
public class ProductStringConverter extends StringConverter<ProductView> {
    private ObservableList<ProductView> products;

    public ProductStringConverter(ObservableList<ProductView> products) {
        this.products = products;
    }

    @Override
    public String toString(ProductView object) {
        return object.getShortName();
    }

    @Override
    public ProductView fromString(String string) {
        return products.stream().filter(productView -> productView.getShortName().equals(string))
                .collect(toList()).get(0);
    }
}
