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
 * Validates that a {@code CLOSED} receipt has a valid
 * {@code closureTime} time stamp. 
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTimeStampValidator.class)
@Documented
public @interface ValidTimeStamp {

    String message() default "{ValidTimestamps.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
