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

import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.enums.ReceiptType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "table")
@javax.persistence.Table(name = "RECEIPT")
@NamedQueries({
    @NamedQuery(name = Receipt.GET_TEST_RECEIPTS,
            query="FROM Receipt r")
})
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_ID"))
public @Data class Receipt extends AbstractEntity {

    public static final String GET_TEST_RECEIPTS = "Receipt.GetTestReceipts";

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "TABLE_ID")
    private Table table;

    @Enumerated(EnumType.STRING)
    private ReceiptType type;

    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;
}
