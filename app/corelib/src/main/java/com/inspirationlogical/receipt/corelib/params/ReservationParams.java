package com.inspirationlogical.receipt.corelib.params;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
@Builder
@JsonDeserialize(builder = ReservationParams.ReservationParamsBuilder.class)
public @Data class ReservationParams {

    @NotEmpty
    private String name;

    private String note;

    private String phoneNumber;

    private int tableNumber;

    private int guestCount;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime endTime;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class ReservationParamsBuilder {
    }
}
