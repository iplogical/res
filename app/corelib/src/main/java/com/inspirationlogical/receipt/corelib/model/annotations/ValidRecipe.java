package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by régiDAGi on 2017. 04. 09..
 */
/**
 * Validates that field {@code parent} contains a
 * valid {@code ProductCategoryType ProductCategory}.
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidRecipeValidator.class)
@Documented
public @interface ValidRecipe {

        String message() default "{ValidRecipe.message}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
}
