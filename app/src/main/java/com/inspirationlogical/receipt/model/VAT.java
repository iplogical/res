package com.inspirationlogical.receipt.model;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.model.enums.VATName;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "VAT")
@NamedQueries({
    @NamedQuery(name = VAT.GET_TEST_VAT_RECORDS,
            query="FROM VAT r")
})
@AttributeOverride(name = "id", column = @Column(name = "VAT_ID"))
public @Data class VAT extends AbstractEntity {

    public static final String GET_TEST_VAT_RECORDS = "VAT.GetTestVATRecords";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "VAT_SERIE_ID")
    private VATSerie serie;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VATName name;

    private double VAT;
}
