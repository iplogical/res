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
 * Validates that there is only one {@code OPEN} receipt
 * at a time.
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidReceiptsValidator.class)
@Documented
public @interface ValidReceipts {

    String message() default "{ValidReceipts.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
