package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    private VATStatus status;

    @Tolerate
    VATSerie(){}

    @Override
    public String toString() {
        return "VATSerie: status=" + status;
    }
}
