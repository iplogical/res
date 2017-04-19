package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

public class ValidProductValidatorProductCategory extends AbstractValidator
    implements ConstraintValidator<ValidProduct, ProductCategory> {
    
    @Override
    public void initialize(ValidProduct annotation) {

    }

    @Override
    public boolean isValid(ProductCategory value, ConstraintValidatorContext context) {
        boolean hasProduct = value.getProduct() != null;
        if (ProductCategoryType.isPseudo(value.getType())) {
            if (hasProduct) {
                addConstraintViolation(context,
                        "The product must not be null for a PSEUDO categories." + value.getName());
            }
            return hasProduct;
        } else {
            if (!hasProduct) {
                addConstraintViolation(context,
                        "The product has to be null for ROOT, AGGREGATE and LEAF categories." + value.getName());
            }
            return !hasProduct;
        }
    }
}
