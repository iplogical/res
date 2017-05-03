package com.inspirationlogical.receipt.reserver.resource;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.reserver.utility.DateParser;
import com.inspirationlogical.receipt.reserver.view.GetReservationsView;

@Path("/reservation")
@Produces(MediaType.TEXT_HTML)
public class ReservationResource {

    private RestaurantService restaurantService;

    @Inject
    public ReservationResource(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GET
    @Path("/{date}")
    public GetReservationsView getReservations(@PathParam("date") Optional<String> dateParameter) throws ParseException {

        final LocalDate localDate = dateParameter.map(DateParser::parseDate).orElseGet(LocalDate::now);

        List<ReservationViewModel> resservations = restaurantService.getReservations(localDate)
                .stream()
                .map(ReservationViewModel::new)
                .collect(Collectors.toList());

        return new GetReservationsView(resservations);
    }

    @POST
    public GetReservationsView addReservation() {
        return null;
    }
}
