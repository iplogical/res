package com.inspirationlogical.receipt.corelib.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@javax.persistence.Table(name = "RESERVATION")
@AttributeOverride(name = "id", column = @Column(name = "RESERVATION_ID"))
public @Data
class Reservation extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "TABLE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table owner;

    @Column(name = "TABLENUMBER")
    private int tableNumber;

    @Column(name = "DATE")
    private LocalDate date;

    @Column(name = "STARTTIME")
    private LocalDateTime startTime;

    @Column(name = "ENDTIME")
    private LocalDateTime endTime;

    @Column(name = "NAME")
    private String name;

    @Column(name = "GUESTCOUNT")
    private int guestCount;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "PHONENUMBER")
    private String phoneNumber;

    @Tolerate
    Reservation() {
    }
}
