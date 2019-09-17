package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import static java.util.stream.Collectors.toList;

public class VatNameStringConverter extends StringConverter<VATName> {

    private ObservableList<VATName> vatNameList;

    public VatNameStringConverter(ObservableList<VATName> vatNameList) {
        this.vatNameList = vatNameList;
    }

    @Override
    public String toString(VATName vatName) {
        return vatName.toI18nString();
    }

    @Override
    public VATName fromString(String string) {
        return vatNameList.stream().filter(vatName -> vatName.toI18nString().equals(string))
                .collect(toList()).get(0);
    }
}
