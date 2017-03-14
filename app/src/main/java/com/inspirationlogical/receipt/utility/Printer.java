package com.inspirationlogical.receipt.utility;

import java.io.InputStream;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public interface Printer {
    String getName();
    void print(InputStream pdf);
    static Printer getDefaultInstance(){
        try{
            return new EpsonTMT20IIPrinter();
        }catch (Exception e){
            return new FilePrinter();
        }
    }
}
