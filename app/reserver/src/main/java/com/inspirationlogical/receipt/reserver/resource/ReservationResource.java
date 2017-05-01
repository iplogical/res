package com.inspirationlogical.receipt.reserver.resource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;

import lombok.SneakyThrows;

@Path("/reservation/{date}")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private static final String DATE_SEPARATOR = "-";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    private RestaurantService restaurantService;

    @Inject
    public ReservationResource(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GET
    public List<ReservationViewModel> getReservations(@PathParam("date") Optional<String> dateParameter) throws ParseException {

        final LocalDate localDate = dateParameter.isPresent() ? parseDate(dateParameter.get()) : LocalDate.now();

        return restaurantService.getReservations(localDate).stream().map(ReservationViewModel::new).collect(Collectors.toList());
    }

    @SneakyThrows
    private LocalDate parseDate(String dateParameter) {
        Date date;
        List<String> dateParts = Arrays.asList(dateParameter.split(DATE_SEPARATOR));

        if (dateParts.size() < 3) {
            String dateString = LocalDate.now().getYear() + DATE_SEPARATOR;

            if (dateParts.size() < 2) {
                dateString += LocalDate.now().getMonthValue() + DATE_SEPARATOR;
            }

            date = DATE_FORMAT.parse(dateString + dateParameter);
        } else {
            date = DATE_FORMAT.parse(dateParameter);
        }

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
