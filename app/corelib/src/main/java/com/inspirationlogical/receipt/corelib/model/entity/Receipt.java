package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "records"})
@javax.persistence.Table(name = "RECEIPT")
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_ID"))
public @Data class Receipt extends AbstractEntity {

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TABLE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReceiptRecord> records = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "VAT_SERIE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private VATSerie VATSerie;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private ReceiptType type;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    @Column(name = "PAYMENTMETHOD")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "OPENTIME")
    private LocalDateTime openTime;

    @Column(name = "CLOSURETIME")
    private LocalDateTime closureTime;

    @Column(name = "USERCODE")
    private int userCode;

    @Column(name = "SUMPURCHASENETPRICE")
    private int sumPurchaseNetPrice;

    @Column(name = "SUMPURCHASEGROSSPRICE")
    private int sumPurchaseGrossPrice;

    @Column(name = "SUMSALENETPRICE")
    private int sumSaleNetPrice;

    @Column(name = "SUMSALEGROSSPRICE")
    private int sumSaleGrossPrice;

    @Column(name = "SUMSALENETORIGINALPRICE")
    private int sumSaleNetOriginalPrice;

    @Column(name = "SUMSALEGROSSORIGINALPRICE")
    private int sumSaleGrossOriginalPrice;

    @Column(name = "DISCOUNTPERCENT")
    private double discountPercent;

    @Column(name = "DELIVERYTIME")
    private LocalDateTime deliveryTime;

    @Column(name = "ISDELIVERED")
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
