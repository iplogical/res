package com.inspirationlogical.receipt.model;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

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

    @OneToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @NotEmpty
    private String name;
}
