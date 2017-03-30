package com.inspirationlogical.receipt.corelib.model.entity;

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

import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "VAT")
@NamedQueries({
    @NamedQuery(name = VAT.GET_TEST_VAT_RECORDS,
            query="FROM VAT v"),
    @NamedQuery(name = VAT.GET_VAT_BY_NAME,
            query="FROM VAT v WHERE v.name = :name AND v.status = :status")
})
@AttributeOverride(name = "id", column = @Column(name = "VAT_ID"))
public @Data class VAT extends AbstractEntity {

    public static final String GET_TEST_VAT_RECORDS = "VAT.GetTestVATRecords";
    public static final String GET_VAT_BY_NAME = "VAT.GetVATByName";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "VAT_SERIE_ID")
    private VATSerie serie;

    @NotNull
    @Enumerated(EnumType.STRING)
    private VATName name;

    @NotNull
    @Enumerated(EnumType.STRING)
    VATStatus status;

    private double VAT;

    @Tolerate
    VAT(){}
}
