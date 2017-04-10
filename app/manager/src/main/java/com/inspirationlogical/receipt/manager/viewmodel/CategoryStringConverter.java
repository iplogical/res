package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import static java.util.stream.Collectors.toList;

/**
 * Created by régiDAGi on 2017. 04. 11..
 */
public class CategoryStringConverter extends StringConverter<ProductCategoryView> {
    private ObservableList<ProductCategoryView> parentCategories;

    public CategoryStringConverter(ObservableList<ProductCategoryView> parentCategories) {
        this.parentCategories = parentCategories;
    }

    @Override
    public String toString(ProductCategoryView object) {
        return object.getCategoryName();
    }

    @Override
    public ProductCategoryView fromString(String string) {
        return parentCategories.stream().filter(productCategoryView -> productCategoryView.getCategoryName().equals(string))
                .collect(toList()).get(0);
    }
}
