package com.inspirationlogical.receipt.corelib.utility;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResult {

    private boolean valid;
    private String errorMessage;
}
