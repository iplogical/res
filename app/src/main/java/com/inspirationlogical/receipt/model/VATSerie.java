package com.inspirationlogical.receipt.model;

import java.util.Collection;

import javax.persistence.*;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.br.TituloEleitoral;

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

    @OneToMany(mappedBy = "serie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<VAT> vat;

    @Tolerate
    VATSerie(){}
}
