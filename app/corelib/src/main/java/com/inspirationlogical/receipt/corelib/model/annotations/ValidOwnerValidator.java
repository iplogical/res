package com.inspirationlogical.receipt.corelib.model.annotations;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidOwnerValidator extends AbstractValidator
    implements ConstraintValidator<ValidOwner, Receipt> {

    @Override
    public void initialize(ValidOwner constraintAnnotation) {

    }

    @Override
    public boolean isValid(Receipt value, ConstraintValidatorContext context) {
        ReceiptType type = value.getType();
        TableType ownerType = value.getOwner().getType();

        switch (type) {
            case SALE:
                if (ownerType == TableType.NORMAL
                        || ownerType == TableType.FREQUENTER
                        || ownerType == TableType.EMPLOYEE
                        || ownerType == TableType.ORPHANAGE) {
                    return true;
                } else {
                    addConstraintViolation(context,
                            "The SALE receipt has to be owned by NORMAL/AGGREGATE/CONSUMED/LOITERER/FREQUENTER/EMPLOYEE/ORPHANAGE table but found:" + ownerType);
                    return false;
                }
            case PURCHASE:
                if (ownerType != TableType.PURCHASE) {
                    addConstraintViolation(context,
                            "The PURCHASE receipt has to be owned by the PURCHASE table but found:" + ownerType);
                    return false;
                } else return true;
            case INVENTORY:
                if (ownerType != TableType.INVENTORY) {
                    addConstraintViolation(context,
                            "The INVENTORY receipt has to be owned by the INVENTORY table but found:" + ownerType);
                    return false;
                } else return true;
            case DISPOSAL:
                if (ownerType != TableType.DISPOSAL) {
                    addConstraintViolation(context,
                            "The DISPOSAL receipt has to be owned by the DISPOSAL table but found:" + ownerType);
                    return false;
                } else return true;
            case OTHER:
                if (ownerType != TableType.OTHER) {
                    addConstraintViolation(context,
                            "The OTHER receipt has to be owned by the OTHER table but found:" + ownerType);
                    return false;
                } else return true;
            default:
                return true;
        }
    }
}
