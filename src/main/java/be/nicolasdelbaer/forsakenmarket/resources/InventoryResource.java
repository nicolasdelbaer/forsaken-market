package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.exceptions.CannotDiscardItemException;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/inventory")
public class InventoryResource {

    @Inject InventoryService inventoryService;
    @Context SecurityContext securityContext;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/discard-item/{id}")
    @Operation(summary = "Sell an item", description = "Sell an item from their inventory")
    public Response discardItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            inventoryService.discardItem(playerSession.id(), itemId);
            response = Response.ok().build();
        } catch (CannotDiscardItemException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
        return response;
    }
}
