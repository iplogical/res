package com.inspirationlogical.receipt.corelib.utility;

/**
 * Created by Ferenc on 2017. 04. 14..
 */
// From boost c++ libraries
public class Hash {
    public  static <T> int combine(int seed, T object){
        seed ^= object.hashCode() + 0x9e3779b9 + (seed<<6) + (seed>>2);
        return  seed;
    }
}
