package com.inspirationlogical.receipt.corelib.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates that field {@code owner} contains a 
 * valid {@code Table}. The type of the {@code Receipt} and
 * the {@code Table} objects shall match.
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidOwnerValidator.class)
@Documented
public @interface ValidOwner {

    String message() default "{ValidOwner.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
