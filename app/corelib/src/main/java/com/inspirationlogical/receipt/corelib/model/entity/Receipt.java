package com.inspirationlogical.receipt.corelib.model.entity;

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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.entity.Receipt.GRAPH_RECEIPT_AND_RECORDS;

@Entity
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "records"})
@javax.persistence.Table(name = "RECEIPT")
@NamedQueries({
    @NamedQuery(name = Receipt.GET_RECEIPTS,
            query="FROM Receipt r ORDER BY r.openTime ASC"),
    @NamedQuery(name = Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER,
            query="SELECT r FROM Receipt r WHERE r.status=:status AND r.owner.number=:number"),
    @NamedQuery(name = Receipt.GET_RECEIPT_BY_CLOSURE_TIME_AND_TYPE,
            query="SELECT r FROM Receipt r WHERE r.closureTime>:startTime AND r.closureTime<:endTime AND r.type=:type"),
    @NamedQuery(name = Receipt.GET_RECEIPT_BY_OPEN_TIME,
            query="SELECT r FROM Receipt r WHERE r.openTime>:startTime AND r.openTime<:endTime")
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

    public static final String GET_RECEIPTS = "Receipt.GetReceipts";
    public static final String GET_RECEIPT_BY_STATUS_AND_OWNER = "Receipt.GetActiveReceipt";
    public static final String GET_RECEIPT_BY_CLOSURE_TIME_AND_TYPE = "Receipt.GetReceiptByClosureTime";
    public static final String GET_RECEIPT_BY_OPEN_TIME = "Receipt.GetReceiptByOpenTime";
    public static final String GRAPH_RECEIPT_AND_RECORDS = "Receipt.GraphReceiptAndRecords";
    private static final Receipt PROTOTYPE = new Receipt();

    public static ReceiptBuilder builder() {
        return PROTOTYPE.toBuilder();
    }

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TABLE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReceiptRecord> records = new ArrayList<>();

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

    private int sumSaleNetOriginalPrice;

    private int sumSaleGrossOriginalPrice;

    private double discountPercent;

    private LocalDateTime deliveryTime;

    private boolean isDelivered;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(nullable = true)),
        @AttributeOverride(name = "address", column = @Column(nullable = true)),
        @AttributeOverride(name = "TAXNumber", column = @Column(nullable = true))
    })
    private Client client;

    @Tolerate
    Receipt(){}

    public static Receipt cloneReceipt(Receipt receipt) {
        Receipt newReceipt = new Receipt();
        newReceipt.setRecords(new ArrayList<>());
        newReceipt.setType(receipt.getType());
        newReceipt.setStatus(receipt.getStatus());
        newReceipt.setPaymentMethod(receipt.getPaymentMethod());
        newReceipt.setOpenTime(receipt.getOpenTime());
        newReceipt.setClosureTime(receipt.getClosureTime());
        newReceipt.setUserCode(receipt.getUserCode());
        newReceipt.setSumPurchaseNetPrice(receipt.getSumPurchaseNetPrice());
        newReceipt.setSumPurchaseGrossPrice(receipt.getSumPurchaseGrossPrice());
        newReceipt.setSumSaleNetPrice(receipt.getSumSaleNetPrice());
        newReceipt.setSumSaleGrossPrice(receipt.getSumSaleGrossPrice());
        newReceipt.setDiscountPercent(receipt.getDiscountPercent());
        return newReceipt;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "table=" + owner.toString() +
                ", type=" + type +
                ", status=" + status +
                ", paymentMethod=" + paymentMethod +
                ", openTime=" + openTime +
                ", closureTime=" + closureTime +
                ", userCode=" + userCode +
                ", sumPurchaseNetPrice=" + sumPurchaseNetPrice +
                ", sumPurchaseGrossPrice=" + sumPurchaseGrossPrice +
                ", sumSaleNetPrice=" + sumSaleNetPrice +
                ", sumSaleGrossPrice=" + sumSaleGrossPrice +
                ", discountPercent=" + discountPercent +
                '}';
    }
}
