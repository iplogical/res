package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

public class ValidProductValidatorProductCategory 
    implements ConstraintValidator<ValidProduct, ProductCategory> {
    
    @Override
    public void initialize(ValidProduct annotation) {

    }

    @Override
    public boolean isValid(ProductCategory value, ConstraintValidatorContext context) {
        if(value.getType() == ProductCategoryType.PSEUDO) {
            if(value.getProduct() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "The product must not be null for a PSEUDO categories."
                )
                .addConstraintViolation();
                return false;
            } else {
                return true;
            }
        } else {
            if(value.getProduct() == null) {
                return true;
            } else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "The product has to be null for ROOT, AGGREGATE and LEAF categories."
                )
                .addConstraintViolation();
                return false;
            }
        }
    }
}
