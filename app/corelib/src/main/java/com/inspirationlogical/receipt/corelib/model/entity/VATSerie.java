package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.util.Collection;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "vat")
@Table(name = "VAT_SERIE")
@AttributeOverride(name = "id", column = @Column(name = "VAT_SERIE_ID"))
public @Data
class VATSerie extends AbstractEntity {

    @OneToMany(mappedBy = "serie", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private Collection<VAT> vat;

    @Enumerated(EnumType.STRING)
    private VATStatus status;

    @Tolerate
    VATSerie() {
    }

    @Override
    public String toString() {
        return "VATSerie: status=" + status;
    }
}
