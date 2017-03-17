package com.inspirationlogical.receipt.corelib.model.annotations;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by Bálint on 2017.03.17..
 */
public abstract class AbstractValidator {
    protected void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
