package com.inspirationlogical.receipt.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.model.Receipt;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;

public class ValidTimeStampValidator 
    implements ConstraintValidator<ValidTimeStamp, Receipt>{

    @Override
    public void initialize(ValidTimeStamp constraintAnnotation) {

    }

    @Override
    public boolean isValid(Receipt value, ConstraintValidatorContext context) {
        if(value.getStatus() == ReceiptStatus.CLOSED) {
            if(value.getClosureTime() == null) {
                addConstraintViolation(context,
                        "The CLOSED receipt shall have a closure time.");
                return false;
            }
            return true;
        } else if(value.getStatus() == ReceiptStatus.OPEN) {
            if(value.getClosureTime() != null) {
                addConstraintViolation(context,
                        "The OPEN receipt shall not have a closure time.");
                return false;
            }
            return true;
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
