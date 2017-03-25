package com.inspirationlogical.receipt.corelib.utility.printing;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.inspirationlogical.receipt.corelib.exception.PrinterClassNotFoundException;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import lombok.NoArgsConstructor;

import java.io.InputStream;

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


