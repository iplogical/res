package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "vat")
@Table(name = "VAT_SERIE")
@AttributeOverride(name = "id", column = @Column(name = "VAT_SERIE_ID"))
public @Data
class VATSerie extends AbstractEntity {

    @OneToMany(mappedBy = "serie", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private List<VAT> vat;

    @Column(name = "STATUS")
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
