package com.inspirationlogical.receipt.corelib.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidOwner;
import com.inspirationlogical.receipt.corelib.model.annotations.ValidReceipts;
import com.inspirationlogical.receipt.corelib.model.annotations.ValidTimeStamp;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GRAPH_RECEIPT_AND_RECORDS;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "records"})
@javax.persistence.Table(name = "RECEIPT")
@NamedQueries({
    @NamedQuery(name = Receipt.GET_TEST_RECEIPTS,
            query="FROM Receipt r"),
    @NamedQuery(name = Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER,
            query="SELECT DISTINCT r FROM Receipt r WHERE r.status=:status AND r.owner.number=:number"),
    @NamedQuery(name = Receipt.GET_RECEIPT_BY_CLOSURE_TIME_AND_TYPE,
            query="SELECT r FROM Receipt r WHERE r.closureTime>:closureTime and r.type=:type"),
    @NamedQuery(name = Receipt.GET_RECEIPT_BY_OPEN_TIME_AND_TYPE,
            query="SELECT r FROM Receipt r WHERE r.openTime>:openTime and r.type=:type"),
})
@NamedEntityGraphs({
        @NamedEntityGraph(name = GRAPH_RECEIPT_AND_RECORDS,
                attributeNodes = @NamedAttributeNode(value = ("records"), subgraph = "records"),
                subgraphs = @NamedSubgraph(name = "records", attributeNodes = @NamedAttributeNode("product")))
})
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_ID"))
@ValidOwner
@ValidTimeStamp
@ValidReceipts
public @Data class Receipt extends AbstractEntity {

    public static final String GET_TEST_RECEIPTS = "Receipt.GetTestReceipts";
    public static final String GET_RECEIPT_BY_STATUS_AND_OWNER = "Receipt.GetActiveReceipt";
    public static final String GET_RECEIPT_BY_CLOSURE_TIME_AND_TYPE = "Receipt.GetReceiptByClosureTime";
    public static final String GET_RECEIPT_BY_OPEN_TIME_AND_TYPE = "Receipt.GetReciptByOpenTime";
    public static final String GRAPH_RECEIPT_AND_RECORDS = "Receipt.GraphReceiptAndRecords";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "TABLE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private List<ReceiptRecord> records;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "VAT_SERIE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private VATSerie VATSerie;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReceiptType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotNull
    private LocalDateTime openTime;

    private LocalDateTime closureTime;

    private int userCode;

    private int sumPurchaseNetPrice;

    private int sumPurchaseGrossPrice;

    private int sumSaleNetPrice;

    private int sumSaleGrossPrice;

    private double discountPercent;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(nullable = true)),
        @AttributeOverride(name = "address", column = @Column(nullable = true)),
        @AttributeOverride(name = "TAXNumber", column = @Column(nullable = true))
    })
    private Client client;

    @Tolerate
    Receipt(){}
}
