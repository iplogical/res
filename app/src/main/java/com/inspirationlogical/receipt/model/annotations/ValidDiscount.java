package com.inspirationlogical.receipt.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates that field {@code discountPercent} and 
 * {@code discountAbsolute} don't have a value simultaneously. 
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidDiscountValidator.class)
@Documented
public @interface ValidDiscount {

    String message() default "{ValidDiscount.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
