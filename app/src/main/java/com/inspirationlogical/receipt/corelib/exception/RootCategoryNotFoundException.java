package com.inspirationlogical.receipt.corelib.exception;

/**
 * Created by Bálint on 2017.03.20..
 */
public class RootCategoryNotFoundException extends EntityNotFoundException {

    public RootCategoryNotFoundException() {
        super("Root ProductCategory");
    }
}

