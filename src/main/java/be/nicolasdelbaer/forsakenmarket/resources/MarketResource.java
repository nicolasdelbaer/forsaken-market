package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.exceptions.BadItemOwnerException;
import be.nicolasdelbaer.forsakenmarket.exceptions.MaxRerollReachedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketPriceRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.services.InventoryService;
import be.nicolasdelbaer.forsakenmarket.services.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/market")
@Tag(name = "Market", description = "Buy & sell operations")
public class MarketResource {

    @Inject private MarketService marketService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/buy-item/{id}")
    @Operation(summary = "Buy an item", description = "Buy an item from the market")
    public Response buyItem(@PathParam("id") Long id) {
        Response response;
        try {
            marketService.buyItem(1, id); //TODO get playerId from session
            response = Response.ok().build();
        } catch (PlayerInsufficientFundsException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "insufficient funds").build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "incorrect data").build();
        }
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sell-item/{id}")
    @Operation(summary = "Sell an item", description = "Sell an item from their inventory")
    public Response sellItem(@PathParam("id") Long id) {
        Response response;
        try {
            marketService.sellItem(1, id); //TODO get playerId from session
            response = Response.ok().build();
        } catch (BadItemOwnerException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "player doesn't own the item").build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "incorrect data").build();
        }
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reroll-item/{id}")
    @Operation(summary = "Reroll an item", description = "Reroll an item from the market")
    public Response rerollItem(@PathParam("id") Long id) {
        Response response;
        try {
            marketService.rerollItem(1, id); //TODO get playerId from session
            response = Response.ok().build();
        } catch (PlayerInsufficientFundsException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "insufficient funds").build();
        } catch (MaxRerollReachedException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "max rerolls reached").build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "incorrect data").build();
        }
        return response;
    }
}