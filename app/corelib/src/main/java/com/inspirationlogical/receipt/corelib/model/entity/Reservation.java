package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Calendar;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@javax.persistence.Table(name = "RESERVATION")
@NamedQueries({
    @NamedQuery(name = Reservation.GET_TEST_RESERVATIONS,
            query="FROM Reservation r")
})
@AttributeOverride(name = "id", column = @Column(name = "RESERVATION_ID"))
public @Data class Reservation extends AbstractEntity {

    public static final String GET_TEST_RESERVATIONS = "Reservation.GetTestReservations";

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "TABLE_ID")
    private Table owner;

    @Min(1)
    private int tableNumber;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endTime;

    @NotEmpty
    private String name;

    private String note;

    @Tolerate
    Reservation(){}
}
