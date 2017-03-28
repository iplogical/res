package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;

public class ValidTimeStampValidator extends AbstractValidator
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
}