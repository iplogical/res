package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ValidTablesValidator 
    implements ConstraintValidator<ValidTables, Object> {

    int purchase;
    int inventory;
    int disposal;
    int other;

    @Override
    public void initialize(ValidTables constraintAnnotation) {

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
        return isValid(value.getOwner(), context);
    }

    public boolean isValid(Restaurant value, ConstraintValidatorContext context) {
        getTableTypes(value);
        return assertConstraints(context);
    }

    private void getTableTypes(Restaurant value) {
        purchase = 0;
        inventory = 0;
        disposal = 0;
        other = 0;

        value.getTable().forEach(table -> {

            if(table.getType() == TableType.PURCHASE) purchase++;
            if(table.getType() == TableType.INVENTORY) inventory++;
            if(table.getType() == TableType.DISPOSAL) disposal++;
            if(table.getType() == TableType.OTHER) other++;
        });
    }

    private boolean assertConstraints(ConstraintValidatorContext context) {
        if(purchase != 1) {
            addConstraintViolation(context,
                    "The number of PURCHASE type tables has to be exactly 1, but was:" + purchase);
            return false;
        } else if(inventory != 1) {
            addConstraintViolation(context,
                    "The number of INVENTORY type tables has to be exactly 1, but was:" + inventory);
            return false;
        } else if(disposal != 1) {
            addConstraintViolation(context,
                    "The number of DISPOSAL type tables has to be exactly 1, but was:" + disposal);
            return false;
        } else if(other != 1) {
            addConstraintViolation(context,
                    "The number of OTHER type tables has to be exactly 1, but was:" + disposal);
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
