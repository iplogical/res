package com.inspirationlogical.receipt.corelib.model.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.*;

/**
 * Validates that field {@code product} is not null if
 * field {@code type} has value {@code ProductCategoryType.PSEUDO}.
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidProductValidatorProductCategory.class, ValidProductValidatorReceiptRecord.class})
@Documented
public @interface ValidProduct {

    String message() default "{NotNullIfPseudo.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
