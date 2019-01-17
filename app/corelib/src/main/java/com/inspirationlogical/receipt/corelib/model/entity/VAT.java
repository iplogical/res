package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "VAT")
@NamedQueries({
        @NamedQuery(name = VAT.GET_TEST_VAT_RECORDS,
                query = "FROM VAT v"),
        @NamedQuery(name = VAT.GET_VAT_BY_NAME,
                query = "FROM VAT v WHERE v.name = :name AND v.status = :status")
})
@AttributeOverride(name = "id", column = @Column(name = "VAT_ID"))
public @Data
class VAT extends AbstractEntity {

    public static final String GET_TEST_VAT_RECORDS = "VAT.GetTestVATRecords";
    public static final String GET_VAT_BY_NAME = "VAT.GetVATByName";

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "VAT_SERIE_ID")
    private VATSerie serie;

    @Enumerated(EnumType.STRING)
    private VATName name;

    @Enumerated(EnumType.STRING)
    VATStatus status;

    private double VAT;

    @Tolerate
    VAT() {
    }
}
