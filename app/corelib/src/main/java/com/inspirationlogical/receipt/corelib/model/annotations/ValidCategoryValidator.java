package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ValidCategoryValidator  extends AbstractValidator
    implements ConstraintValidator<ValidCategory, Object> {

    @Override
    public void initialize(ValidCategory constraintAnnotation) {

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

    public boolean isValid(Product value, ConstraintValidatorContext context) {
        if(value.getCategory() == null) {
            addConstraintViolation( context, "The product category shall not be null.");
            return false;
        } else if(value.getCategory().getType() != ProductCategoryType.PSEUDO) {
            addConstraintViolation( context, "The product category has to be of PSEUDO type.");
            return false;
        }
        return true;
    }

    public boolean isValid(ProductCategory value, ConstraintValidatorContext context) {
        if(value.getType().equals(ProductCategoryType.PSEUDO)) {
            return isValid(value.getProduct(),context);
        } else return true;
    }
}
