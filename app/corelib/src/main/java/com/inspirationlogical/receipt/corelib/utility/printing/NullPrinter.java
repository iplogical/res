package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.InputStream;

/**
 * Created by Ferenc on 2017. 04. 15..
 */
public class NullPrinter implements Printer {
    @Override
    public String getName() {
        return "NullPrinter";
    }

    @Override
    public void print(InputStream pdf) {

    }
}
