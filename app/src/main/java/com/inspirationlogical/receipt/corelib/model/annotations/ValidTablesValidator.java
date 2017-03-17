package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ValidTablesValidator extends AbstractValidator
    implements ConstraintValidator<ValidTables, Object> {

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
        List<Map.Entry<TableType, Long>> entries = value.getTable().stream()
                .filter(table -> TableType.isSpecial(table.getType()))
                .collect(Collectors.groupingBy(t -> t.getType(), Collectors.counting()))
            .entrySet().stream().collect(Collectors.toList());
        entries.sort(TableType.getComparator());
        Iterator<Map.Entry<TableType, Long>> current = entries.iterator(), expected = TableType.specialTypes().iterator();
        while(expected.hasNext()) {
            Map.Entry<TableType, Long> expectedEntry = expected.next();
            if(!current.hasNext()) {
                addConstraintViolation(context,
                        "The number of "+ expectedEntry.getKey() +" type tables has to be exactly 1, but was: " + 0);
                return false;
            }
            Map.Entry<TableType, Long> currentEntry = current.next();
            if(!currentEntry.equals(expectedEntry)) {
                addConstraintViolation(context,
                        "The number of "+ currentEntry.getKey() +" type tables has to be exactly 1, but was: " + currentEntry.getValue());
                return false;
            }
        }
        return true;
    }
}
