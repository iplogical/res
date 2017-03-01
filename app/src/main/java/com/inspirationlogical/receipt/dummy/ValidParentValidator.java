package com.inspirationlogical.receipt.dummy;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidParentValidator
    implements ConstraintValidator<ValidParent, ProductCategory> {

    @Override
    public void initialize(ValidParent constraintAnnotation) {
        
    }

    @Override
    public boolean isValid(ProductCategory value, ConstraintValidatorContext context) {

        switch (value.getType()) {
            case ROOT:
                if(value.getParent() != null) {
                    addConstraintViolation(context, "The ROOT category must not have a parent category.");
                    return false;
                }
                return true;
            case AGGREGATE:
                if(value.getParent() == null) {
                    addConstraintViolation(context, "The AGGREGATE category must have a parent category.");
                    return false;
                } else if(value.getParent().getType() == ProductCategoryType.LEAF) {
                    addConstraintViolation(context, "The AGGREGATE category must have a ROOT or AGGREGATE parent category.");
                    return false;                
                } else if(value.getParent().getType() == ProductCategoryType.PSEUDO) {
                    addConstraintViolation(context, "The AGGREGATE category must have a ROOT or AGGREGATE parent category.");
                    return false;                
                }
                return true;
            case LEAF:
                if(value.getParent() == null) {
                    addConstraintViolation(context, "The LEAF category must have a parent category.");
                    return false;
                }
                else if(value.getParent().getType() != ProductCategoryType.AGGREGATE) {
                    addConstraintViolation(context, "The LEAF category must have an AGGREGATE parent category.");
                    return false;                
                }
                return true;
            case PSEUDO:
                if(value.getParent() == null) {
                    addConstraintViolation(context, "The PSEUDO category must have a parent category.");
                    return false;
                }
                else if(value.getParent().getType() != ProductCategoryType.LEAF) {
                    addConstraintViolation(context, "The PSEUDO category must have an LEAF parent category.");
                    return false;                
                }
                return true;
            case PSEUDO_DELETED:
            default:
                return true;
        }
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
