package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;

public class ValidProductValidatorReceiptRecord extends AbstractValidator
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
}
