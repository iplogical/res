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
 * Validates that field {@code paymentMethod} contains a 
 * valid {@code PaymentMethod}.
 **/
@Target(value = {TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidPaymentMethodValidator.class)
@Documented
public @interface ValidPaymentMethod {

    String message() default "{ValidParent.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
