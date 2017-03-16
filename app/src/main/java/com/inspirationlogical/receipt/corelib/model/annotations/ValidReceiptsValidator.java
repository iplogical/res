package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;

import java.util.stream.Collectors;

public class ValidReceiptsValidator 
    implements ConstraintValidator<ValidReceipts, Table> {

    private int open;

    @Override
    public void initialize(ValidReceipts constraintAnnotation) {

    }

    @Override
    public boolean isValid(Table value, ConstraintValidatorContext context) {
        open = 0;
        if(value.getReceipt() == null) {
            return true;
        }
        value.getReceipt().stream()
                .filter(receipt -> receipt.getStatus().equals(ReceiptStatus.OPEN))
                .map(receipt -> open++)
                .collect(Collectors.toList());
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
