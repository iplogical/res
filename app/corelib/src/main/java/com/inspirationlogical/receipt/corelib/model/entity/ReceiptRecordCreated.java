package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidProduct;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by TheDagi on 2017. 05. 06..
 */
@Entity
@Builder
@javax.persistence.Table(name = "RECEIPT_RECORD_CREATED")
@NamedQueries({
        @NamedQuery(name = ReceiptRecordCreated.GET_TEST_RECEIPT_RECORDS_CREATED,
                query="FROM ReceiptRecordCreated r")
})
@AttributeOverride(name = "id", column = @Column(name = "RECEIPT_RECORD_CREATED_ID"))
public @Data class ReceiptRecordCreated extends AbstractEntity {

    public static final String GET_TEST_RECEIPT_RECORDS_CREATED = "ReceiptRecordCreated.Test";

    @NotNull
    @ManyToOne(fetch=FetchType.EAGER, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "RECEIPT_RECORD_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ReceiptRecord owner;

    private LocalDateTime created;

    @Tolerate
    ReceiptRecordCreated(){}

    @Override
    public String toString() {
        return owner.getName() + ": " + created.toString();
    }

}
