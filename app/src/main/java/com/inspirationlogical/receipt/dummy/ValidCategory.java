package com.inspirationlogical.receipt.dummy;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates that field {@code parent} contains a 
 * valid {@code ProductCategoryType ProductCategory}.
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidCategoryValidator.class)
@Documented
public @interface ValidCategory {

    String message() default "{ValidCategory.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
