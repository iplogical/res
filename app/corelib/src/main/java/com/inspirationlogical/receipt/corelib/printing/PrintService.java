package com.inspirationlogical.receipt.corelib.printing;

import java.io.InputStream;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PrintService {

    @Getter
    @Inject
    private Printer printer;

    public static PrintService create(){
        try {
            return PrintingInjector.getInjector().getInstance(PrintService.class);
        }catch (Exception e){
            return null;
        }
    }

    public void print(InputStream pdf){
        printer.print(pdf);
    }
}


