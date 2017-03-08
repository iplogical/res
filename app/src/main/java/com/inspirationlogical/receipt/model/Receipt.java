package com.inspirationlogical.receipt.model;

import java.util.Collection;
import java.util.Calendar;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.model.annotations.ValidOwner;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.enums.ReceiptType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "records"})
@javax.persistence.Table(name = "RECEIPT")
@NamedQueries({
    @NamedQuery(name = Receipt.GET_TEST_RECEIPTS,
            query="FROM Receipt r")
})
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_ID"))
@ValidOwner
public @Data class Receipt extends AbstractEntity {

    public static final String GET_TEST_RECEIPTS = "Receipt.GetTestReceipts";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "TABLE_ID")
    private Table owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<ReceiptRecord> records;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "VAT_SERIE_ID")
    private VATSerie VATSerie;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReceiptType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar openTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar closureTime;

    private int userCode;

    private int sumPurchaseNetPrice;

    private int sumPurchaseGrossePrice;

    private int sumSaleNetPrice;

    private int sumSaleGrossePrice;

    private double discountPercent;

    private int discountAbsolute;

    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(nullable = false)),
        @AttributeOverride(name = "address", column = @Column(nullable = false)),
        @AttributeOverride(name = "TAXNumber", column = @Column(nullable = false))
    })
    private Client client;

}
