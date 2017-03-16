package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;

import javax.persistence.*;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "vat")
@Table(name = "VAT_SERIE")
@NamedQueries({
    @NamedQuery(name = VATSerie.GET_TEST_VAT_SERIES,
            query="FROM VATSerie vs")
})
@AttributeOverride(name = "id", column = @Column(name = "VAT_SERIE_ID"))
public @Data class VATSerie extends AbstractEntity {

    public static final String GET_TEST_VAT_SERIES = "VATSerie.GetTestVATSeries";
    public static final String GET_VAT_SERIE = "VATSerie.GetTestVATSeries";

    @OneToMany(mappedBy = "serie", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<VAT> vat;

    @Tolerate
    VATSerie(){}
}
