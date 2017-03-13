package com.inspirationlogical.receipt.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException() {
        super("Restaurant entry could not be retrieved from database!");
    }
}
