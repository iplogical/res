package com.inspirationlogical.receipt.corelib.model.annotations;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by régiDAGi on 2017. 04. 09..
 */
public class ValidRecipeValidator extends AbstractValidator
        implements ConstraintValidator<ValidRecipe, Object> {

    public void initialize(ValidRecipe constraintAnnotation) {

    }

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
        if(ProductType.doNotNeedReceipt(value.getType())) {
            return true;
        }
        if(value.getRecipes() == null) {
            addConstraintViolation( context, "The recipe shall not be null.");
            return false;
        }
        return true;
    }

    public boolean isValid(Recipe value, ConstraintValidatorContext context) {
        return isValid(value.getOwner(),context);
    }
}
