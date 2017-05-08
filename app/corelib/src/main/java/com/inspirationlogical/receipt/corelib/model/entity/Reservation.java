package com.inspirationlogical.receipt.corelib.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
    @NamedQuery(name = Reservation.GET_RESERVATIONS,
            query="FROM Reservation r"),
    @NamedQuery(name = Reservation.GET_RESERVATIONS_BY_DATE,
            query="FROM Reservation r WHERE r.date =:date"),
    @NamedQuery(name = Reservation.GET_RESERVATION_BY_ID,
            query="FROM Reservation r WHERE r.id =:id")
})
@AttributeOverride(name = "id", column = @Column(name = "RESERVATION_ID"))
public @Data class Reservation extends AbstractEntity {

    public static final String GET_RESERVATIONS = "Reservation.GetReservations";
    public static final String GET_RESERVATIONS_BY_DATE = "Reservation.GetReservationsByDate";
    public static final String GET_RESERVATION_BY_ID = "Reservation.GetReservationById";

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "TABLE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table owner;

    @Min(1)
    private int tableNumber;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotEmpty
    private String name;

    private int guestCount;

    private String note;

    private String phoneNumber;

    @Tolerate
    Reservation(){}
}
