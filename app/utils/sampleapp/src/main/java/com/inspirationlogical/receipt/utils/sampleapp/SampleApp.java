package com.inspirationlogical.receipt.utils.sampleapp;


import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.service.RestaurantServicesImpl;

public class SampleApp {

    public static void main(String[] args) {
        System.out.println(new RestaurantServicesImpl(
                EntityManagerProvider.getEntityManager()).getActiveRestaurant().getCompanyName());
    }
}

