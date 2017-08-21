package com.inspirationlogical.receipt.corelib.utility.printing;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.inspirationlogical.receipt.corelib.exception.FormatterClassNotFound;
import com.inspirationlogical.receipt.corelib.exception.PrinterClassNotFoundException;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
class PrintingInjector extends AbstractModule {

    private static Injector injector;
    public static Injector getInjector() {return injector;}
    static {
        injector = Guice.createInjector(new PrintingInjector());
    }

    private <ExceptionT extends RuntimeException> Class getClass(String name, Class<ExceptionT> clazz){
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            ExceptionT toThrow = null;
            try {
                toThrow = clazz.newInstance();
            } catch (InstantiationException e1) {
                throw new RuntimeException(e1);
            } catch (IllegalAccessException e1) {
                throw new RuntimeException(e1);
            }
            throw toThrow;
        }
    }

    @Override
    protected void configure() {
        Class printer = getClass(Resources.CONFIG.getString("ReceiptPrinterClass"),PrinterClassNotFoundException.class);
        Class formatter  = getClass(Resources.CONFIG.getString("ReceiptFormatterClass"), FormatterClassNotFound.class);
        bind(Printer.class).to(printer);
        bind(ReceiptFormatter.class).to(formatter);
    }
}