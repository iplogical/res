package com.inspirationlogical.receipt.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.model.entity.ReceiptRecord;

public class ValidDiscountValidator
    implements ConstraintValidator<ValidDiscount, ReceiptRecord> {

    @Override
    public void initialize(ValidDiscount constraintAnnotation) {

    }

    @Override
    public boolean isValid(ReceiptRecord value, ConstraintValidatorContext context) {
        if(value.getDiscountAbsolute() != null && value.getDiscountPercent() != null) {
            addConstraintViolation(context,
                    "There cannot be percent and absolute discounts simultaneously.");
            return false;
        }
        if(value.getDiscountPercent() != null && value.getDiscountPercent() > 100D) {
            addConstraintViolation(context,
                    "The discount cannot be more than 100 % percent.");
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
