package com.inspirationlogical.receipt.exception;

public class RestaurantNotFoundException extends EntityNotFoundException {

    public RestaurantNotFoundException() {
        super("Restaurant");
    }
}
