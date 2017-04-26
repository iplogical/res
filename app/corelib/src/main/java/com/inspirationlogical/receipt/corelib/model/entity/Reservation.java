package com.inspirationlogical.receipt.corelib.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.*;
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
            query="FROM Reservation r"),
    @NamedQuery(name = Reservation.GET_RESERVATIONS_BY_DATE,
            query="FROM Reservation r WHERE r.date =:date"),
})
@AttributeOverride(name = "id", column = @Column(name = "RESERVATION_ID"))
public @Data class Reservation extends AbstractEntity {

    public static final String GET_TEST_RESERVATIONS = "Reservation.GetTestReservations";
    public static final String GET_RESERVATIONS_BY_DATE = "Reservation.GetReservationsByDate";

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "TABLE_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table owner;

    @Min(1)
    private int tableNumber;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startTime;

    private LocalTime endTime;

    @NotEmpty
    private String name;

    private int guestCount;

    private String note;

    @Tolerate
    Reservation(){}
}
