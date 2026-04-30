package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.BadItemOwnershipException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarkeItemDoesNotExistException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MaxRerollReachedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@RequestScoped
@Path("/market")
@Tag(name = "Market", description = "Buy & sell operations")
public class MarketResource {

    @Inject private MarketService marketService;
    @Context private SecurityContext securityContext;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/buy-item/{id}")
    @Operation(summary = "Buy an item", description = "Buy an item from the market")
    public Response buyItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            marketService.buyItem(playerSession.id(), itemId);
            response = Response.ok().build();
        } catch (PlayerInsufficientFundsException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "insufficient funds").build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "incorrect data").build();
        }
        return response;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sell-item/{id}")
    @Operation(summary = "Sell an item", description = "Sell an item from their inventory")
    public Response sellItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            marketService.sellItem(playerSession.id(), itemId);
            response = Response.ok().build();
        } catch (BadItemOwnershipException | CannotSellInactiveItemException | MarketPriceNotFoundException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
        return response;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reroll-item/{id}")
    @Operation(summary = "Reroll an item", description = "Reroll an item from the market")
    public Response rerollItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            marketService.rerollItem(playerSession.id(), itemId);
            response = Response.ok().build();
        } catch (PlayerInsufficientFundsException | MaxRerollReachedException | MarkeItemDoesNotExistException |
                 PlayerNotFoundException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
        return response;
    }
}