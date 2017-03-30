package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Calendar;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidProduct;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "product"})
@ToString(exclude = {"owner", "product"})
@Table(name = "RECEIPT_RECORD")
@NamedQueries({
    @NamedQuery(name = ReceiptRecord.GET_TEST_RECEIPTS_RECORDS,
            query="FROM ReceiptRecord r"),
    @NamedQuery(name = ReceiptRecord.GET_RECEIPTS_RECORDS_BY_TIMESTAMP,
            query="FROM ReceiptRecord r WHERE r.created >:created AND r.name=:name")
})
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_RECORD_ID"))
@ValidProduct
public @Data class ReceiptRecord extends AbstractEntity {

    public static final String GET_TEST_RECEIPTS_RECORDS = "ReceiptRecord.GetTestReceiptsRecords";
    public static final String GET_RECEIPTS_RECORDS_BY_TIMESTAMP = "ReceiptRecord.GetReceiptsRecordByTimeStamp";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "RECEIPT_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Receipt owner;

    @OneToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ReceiptRecordType type;

    @NotEmpty
    private String name;

    private double soldQuantity;

    private int purchasePrice;

    private int salePrice;

    private double VAT;

    @Max(100)
    private double discountPercent;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar created;

    @Tolerate
    ReceiptRecord(){}
}
