package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.InputStream;

import com.google.inject.Inject;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
public class PrintService {
    @Inject
    private Printer printer;

    public String getName(){
        if(printer!=null)return printer.getName();
        else return "UNKNOWN";
    }

    public void print(InputStream pdf){
        if(printer!=null)printer.print(pdf);
    }

    public static PrintService create(){
        try {
            return PrintingInjector.getInjector().getInstance(PrintService.class);
        }catch (Exception e){
            return new PrintService();
        }
    }
}


