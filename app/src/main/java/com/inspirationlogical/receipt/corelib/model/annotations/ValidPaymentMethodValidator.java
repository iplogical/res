package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;

public class ValidPaymentMethodValidator
implements ConstraintValidator<ValidPaymentMethod, Receipt> {

    @Override
    public void initialize(ValidPaymentMethod constraintAnnotation) {

    }

    @Override
    public boolean isValid(Receipt value, ConstraintValidatorContext context) {

        switch(value.getType()) {
        case SALE:
        case PURCHASE:
        case OTHER:
            return checkPaymentMethod(value, true, context);
        case INVENTORY:
        case DISPOSAL:
            return checkPaymentMethod(value, false, context);
        }
        return true;
    }

    private boolean checkPaymentMethod(Receipt value, boolean isNull, ConstraintValidatorContext context) {
        if(isPaymentMethodNull(value) == isNull) {
            addConstraintViolation( context,
                    "The " +value.getType().toString() + "type receipt shall " + isNot(isNull) + " hava a payment method.");
            return false;
        } else {
            return true;
        }
    }

    private String isNot(boolean isNull) {
        if(isNull) {
            return "";
        } else {
            return "NOT";
        }
    }

    private boolean isPaymentMethodNull(Receipt value) {
        return value.getPaymentMethod() == null;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
