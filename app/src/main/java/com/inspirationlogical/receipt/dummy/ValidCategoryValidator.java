package com.inspirationlogical.receipt.dummy;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCategoryValidator 
    implements ConstraintValidator<ValidCategory, Product>{

    @Override
    public void initialize(ValidCategory constraintAnnotation) {

    }

    @Override
    public boolean isValid(Product value, ConstraintValidatorContext context) {
        if(value.getCategory() == null) {
            addConstraintViolation( context, "The product category shall not be null.");
            return false;
        } else if(value.getCategory().getType() != ProductCategoryType.PSEUDO) {
            addConstraintViolation( context, "The product category has to be of PSEUDO type.");
            return false;
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
