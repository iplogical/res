package com.inspirationlogical.receipt.utils.sampleapp;


import com.inspirationlogical.receipt.corelib.utility.BuildSchema;

public class SchemaBuilderApp {

    public static void main(String[] args) {
        BuildSchema buildSchema = new BuildSchema();
        buildSchema.buildTestSchema();
    }
}

