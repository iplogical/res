package com.inspirationlogical.receipt.model;

import java.util.Calendar;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.model.enums.ReceiptRecordType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "product"})
@ToString(exclude = {"owner", "product"})
@Table(name = "RECEIPT_RECORD")
@NamedQueries({
    @NamedQuery(name = ReceiptRecord.GET_TEST_RECEIPTS_RECORDS,
            query="FROM ReceiptRecord r")
})
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_RECORD_ID"))
public @Data class ReceiptRecord extends AbstractEntity {

    public static final String GET_TEST_RECEIPTS_RECORDS = "ReceiptRecord.GetTestReceiptsRecords";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "RECEIPT_ID")
    private Receipt owner;

    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ReceiptRecordType type;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar created;

    @NotEmpty
    private String name;

    private double quantity;

    private int salePrice;

    private double VAT;

    private double discountPercent;

    private double discountAbsolute;
}
