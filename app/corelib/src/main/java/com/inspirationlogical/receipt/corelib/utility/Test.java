package com.inspirationlogical.receipt.corelib.utility;

public class Test {

    private int i;

    static{
        System.out.println("Static");
    }

    {
        System.out.println("Non-static block of " + i);
    }

    public Test(int i) {
        this.i = i;
        System.out.println("Constructor of " + i);
    }

    public static void main(String[] args) {
        Test t = new Test(1);
        Test t2 = new Test(2);
        Test t3 = new Test(3);
    }
}
