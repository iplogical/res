package com.inspirationlogical.receipt.corelib.exception;

public class RestaurantNotFoundException extends EntityNotFoundException {

    public RestaurantNotFoundException() {
        super("Restaurant");
    }
}
