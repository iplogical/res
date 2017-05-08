package com.inspirationlogical.receipt.reserver.resource;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.reserver.utility.DateParser;
import com.inspirationlogical.receipt.reserver.view.GetReservationsByDateView;
import com.inspirationlogical.receipt.reserver.view.GetReservationsView;

@Path("/reservation")
public class ReservationResource {

    private RestaurantService restaurantService;

    @Inject
    public ReservationResource(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GET
    @Path("/api")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ReservationViewModel> getReservations() throws ParseException {

        List<ReservationViewModel> reservations = restaurantService.getReservations()
                .stream()
                .map(ReservationViewModel::new)
                .collect(Collectors.toList());

        return reservations;
    }

    @GET
    public GetReservationsView getReservationsView() throws ParseException {

        return new GetReservationsView();
    }

    @GET
    @Path("/{date}")
    public GetReservationsByDateView getReservationsView(@PathParam("date") Optional<String> dateParameter) throws ParseException {

        final LocalDate localDate = dateParameter.map(DateParser::parseDate).orElseGet(LocalDate::now);

        List<ReservationViewModel> reservations = restaurantService.getReservations(localDate)
                .stream()
                .map(ReservationViewModel::new)
                .collect(Collectors.toList());

        return new GetReservationsByDateView(reservations, localDate);
    }

    @POST
    @Path("/api")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addReservation(@Valid ReservationParams reservationParams) {

        restaurantService.addReservation(reservationParams);
    }

    @PUT
    @Path("/api/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editReservation(@PathParam("id") long reservationId, @Valid ReservationParams reservationParams) {
        restaurantService.updateReservation(reservationId, reservationParams);
    }

    @DELETE
    @Path("/api/{id}")
    public void editReservation(@PathParam("id") long reservationId) {
        restaurantService.deleteReservation(reservationId);
    }
}
