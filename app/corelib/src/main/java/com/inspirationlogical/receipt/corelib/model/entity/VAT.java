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
@AttributeOverride(name = "id", column = @Column(name = "VAT_ID"))
public @Data
class VAT extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "VAT_SERIE_ID")
    private VATSerie serie;

    @Column(name = "NAME")
    @Enumerated(EnumType.STRING)
    private VATName name;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    VATStatus status;

    @Column(name = "VAT")
    private double VAT;

    @Column(name = "CASHIER_NUMBER")
    private int cashierNumber;

    @Column(name = "SERVICE_FEE_CASHIER_NUMBER")
    private int serviceFeeCashierNumber;

    @Tolerate
    VAT() {
    }
}
