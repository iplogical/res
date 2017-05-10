package com.inspirationlogical.receipt.corelib.security.model.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.inspirationlogical.receipt.corelib.model.annotations.AbstractValidator;
import com.inspirationlogical.receipt.corelib.security.Role;

public class ValidRoleValidator extends AbstractValidator
    implements ConstraintValidator<ValidRole, String> {

    @Override
    public void initialize(ValidRole constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.containsAny(value, Role.ADMIN, Role.USER);
    }
}
