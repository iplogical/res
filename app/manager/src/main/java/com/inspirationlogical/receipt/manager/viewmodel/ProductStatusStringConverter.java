package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import static java.util.stream.Collectors.toList;

public class ProductStatusStringConverter  extends StringConverter<ProductStatus> {
    private ObservableList<ProductStatus> productStatus;

    public ProductStatusStringConverter(ObservableList<ProductStatus> productStatus) {
        this.productStatus = productStatus;
    }

    @Override
    public String toString(ProductStatus productStatus) {
        return productStatus.toI18nString();
    }

    @Override
    public ProductStatus fromString(String string) {
        return productStatus.stream().filter(productStatus -> productStatus.toI18nString().equals(string))
                .collect(toList()).get(0);
    }
}
