package com.inspirationlogical.receipt.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.model.entity.Table;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;

public class ValidReceiptsValidator 
    implements ConstraintValidator<ValidReceipts, Table> {

    private int open;

    @Override
    public void initialize(ValidReceipts constraintAnnotation) {

    }

    @Override
    public boolean isValid(Table value, ConstraintValidatorContext context) {
        open = 0;
        value.getReceipt().forEach(receipt -> {
            if(receipt.getStatus() == ReceiptStatus.OPEN) {
                open++;
            }
        });
        if(open > 1) {
            addConstraintViolation(context,
                    "There can be only 0 or 1 open receipt per table, but found: " + open);
            return false;
        } else return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
