package com.inspirationlogical.receipt.utils.sampleapp;


import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.service.RestaurantServiceImpl;
import com.inspirationlogical.receipt.corelib.utility.BuildSchema;

public class SampleApp {

    public static void main(String[] args) {
//        System.out.println(new RestaurantServiceImpl(
//                EntityManagerProvider.getEntityManager()).getActiveRestaurant().getCompanyName());

        BuildSchema buildSchema = new BuildSchema();
        buildSchema.buildTestSchema();
    }
}

