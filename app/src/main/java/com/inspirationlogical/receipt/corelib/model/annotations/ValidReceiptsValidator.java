package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//abstract class TableValidReceiptsValidator implements ConstraintValidator<ValidReceipts, Table>
//{
//    abstract boolean isValidTable(Table value, ConstraintValidatorContext context);
//    abstract void initializeTable(ValidReceipts constraintAnnotation);
//
//    @Override
//    public final void initialize(ValidReceipts constraintAnnotation) {
//        initializeTable(constraintAnnotation);
//    }
//
//    @Override
//    public final boolean isValid(Table value, ConstraintValidatorContext context) {
//        return  isValidTable(value,context);
//    }
//}

public class ValidReceiptsValidator implements ConstraintValidator<ValidReceipts,Object>{

    @Override
    public void initialize(ValidReceipts constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Method isValidMethod = this.getClass().
                    getDeclaredMethod("isValid", value.getClass(), ConstraintValidatorContext.class);
            return (boolean)isValidMethod.invoke(this,value,context);
        }catch (NoSuchMethodException | InvocationTargetException |IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    public boolean isValid(Table value, ConstraintValidatorContext context) {
        if(value.getReceipt() == null) {
            return true;
        }
        List<Receipt> open =  value.getReceipt().stream()
                .filter(receipt -> receipt.getStatus().equals(ReceiptStatus.OPEN))
                .collect(Collectors.toList());
        if(open.size() > 1) {
            addConstraintViolation(context,
                    "There can be only 0 or 1 open receipt per table, but found: " + open);
            return false;
        } else return true;
    }

    public boolean isValid(Receipt value, ConstraintValidatorContext context) {
        return isValid(value.getOwner(),context);
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }


}