package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Calendar;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidProduct;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;

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
@ValidProduct
public @Data class ReceiptRecord extends AbstractEntity {

    public static final String GET_TEST_RECEIPTS_RECORDS = "ReceiptRecord.GetTestReceiptsRecords";

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

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar created;

    @NotEmpty
    private String name;

    private double soldQuantity;

    private int purchasePrice;

    private int salePrice;

    private double VAT;

    @Max(100)
    private double discountPercent;

    @Tolerate
    ReceiptRecord(){}
}
