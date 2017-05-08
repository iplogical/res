package com.inspirationlogical.receipt.corelib.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Table;
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
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "product", "createdList"})
@ToString(exclude = {"owner", "product"})
@Table(name = "RECEIPT_RECORD")
@NamedQueries({
    @NamedQuery(name = ReceiptRecord.GET_TEST_RECEIPT_RECORDS,
            query="FROM ReceiptRecord r"),
    @NamedQuery(name = ReceiptRecord.GET_RECEIPT_RECORDS_BY_TIMESTAMP,
            query="FROM ReceiptRecord r INNER JOIN r.createdList cl WHERE cl.created >:created AND r.name=:name ORDER BY cl.created DESC"),
    @NamedQuery(name = ReceiptRecord.GET_RECEIPT_RECORDS_BY_RECEIPT,
            query="FROM ReceiptRecord r WHERE r.owner.id =:owner_id")
})
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_RECORD_ID"))
@ValidProduct
public @Data class ReceiptRecord extends AbstractEntity {

    public static final String GET_TEST_RECEIPT_RECORDS = "ReceiptRecord.GetTestReceiptsRecords";
    public static final String GET_RECEIPT_RECORDS_BY_TIMESTAMP = "ReceiptRecord.GetReceiptsRecordByTimeStamp";
    public static final String GET_RECEIPT_RECORDS_BY_RECEIPT = "ReceiptRecord.GetReceiptsRecordByReceipt";

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

    private double absoluteQuantity;

    private int purchasePrice;

    private int salePrice;

    private double VAT;

    @Max(100)
    private double discountPercent;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST , CascadeType.REFRESH, CascadeType.REMOVE})
    private List<ReceiptRecordCreated> createdList;

    @Tolerate
    ReceiptRecord(){}
}
