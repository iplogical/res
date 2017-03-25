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
 * Validates that field {@code table} contains a 
 * valid combination of tables, which means that there is
 * exactly one of all three special table kinds.
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTablesValidator.class)
@Documented
public @interface ValidTables {
    
    String message() default "{ValidTables.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
