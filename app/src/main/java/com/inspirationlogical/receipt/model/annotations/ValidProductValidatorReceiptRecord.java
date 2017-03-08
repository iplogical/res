package com.inspirationlogical.receipt.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.model.ReceiptRecord;
import com.inspirationlogical.receipt.model.enums.ReceiptType;

public class ValidProductValidatorReceiptRecord
    implements ConstraintValidator<ValidProduct, ReceiptRecord>{

    @Override
    public void initialize(ValidProduct constraintAnnotation) {

    }

    @Override
    public boolean isValid(ReceiptRecord value, ConstraintValidatorContext context) {
        if(value.getOwner().getType() == ReceiptType.OTHER) {
            if(value.getProduct() != null) {
                addConstraintViolation(context, "The OTHER type receipt record shall not have a product.");
                return false;
            }
        } else {
            if(value.getProduct() == null) {
                addConstraintViolation(context, "The receipt record shall have a product.");
                return false;
            }
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
