package com.inspirationlogical.receipt.utils.sampleapp;


import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.service.RestaurantServiceImpl;

public class SampleApp {

    public static void main(String[] args) {
        System.out.println(new RestaurantServiceImpl(
                EntityManagerProvider.getEntityManager()).getActiveRestaurant().getCompanyName());
    }
}

