package com.inspirationlogical.receipt.reserver.resource;

import static com.inspirationlogical.receipt.corelib.security.Role.ADMIN;
import static com.inspirationlogical.receipt.corelib.security.Role.USER;

import java.net.URI;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.inspirationlogical.receipt.reserver.view.LogoutView;

@Path("/")
public class SecurityResource {

    @GET
    public Response index() {
        URI uri = UriBuilder.fromPath("/login").build();
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/login")
    @RolesAllowed({ADMIN, USER})
    public Response login() {
        URI uri = UriBuilder.fromPath("/reservation").build();
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/logout")
    public LogoutView logout() {
        return new LogoutView();
    }
}
