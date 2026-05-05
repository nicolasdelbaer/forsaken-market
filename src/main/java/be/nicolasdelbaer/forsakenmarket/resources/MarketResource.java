package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketItemDoesNotExistException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MaxRerollReachedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.MarketService;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequestScoped
@Authenticated
@RolesAllowed("Merchant")
@Path("/market")
@Tag(name = "Market", description = "Buy & reroll operations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MarketResource {

    private static final Logger log = LoggerFactory.getLogger(MarketResource.class);
    @Inject private MarketService marketService;
    @Context private SecurityContext securityContext;
    @Inject private GameStateManager gameStateManager;

    @GET
    @Path("/pricelist")
    @Operation(summary = "Market prices for items", description = "Prices with current trend and their values")
    public Response getPriceList() {
        Response response;
        try {
            //TODO use service rather than gameStateManager
            response = Response.ok(gameStateManager.getPricesHistory()).build(); //TODO send back data with results
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.CannotBuyItem).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    @GET
    @Path("/market-evolution/{id}")
    @Operation(summary = "Market prices for items", description = "Prices with current trend and their values")
    public Response getMarketEvolution(@PathParam("id") Long itemId) {
        Response response;
        try {
            response = Response.ok(marketService.getMarketEvolution(itemId)).build(); //TODO send back data with results
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.CannotBuyItem).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    @POST
    @Path("/buy/{id}")
    @Operation(summary = "Buy an item", description = "Buy an item from the market")
    public Response buyItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            marketService.buyItem(playerSession.id(), itemId);
            response = Response.ok().build(); //TODO send back data with results
        } catch (PlayerInsufficientFundsException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.InsufficientFunds).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.CannotBuyItem).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    @POST
    @Path("/reroll/{id}")
    @Operation(summary = "Reroll an item", description = "Reroll an item from the market")
    public Response rerollItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            marketService.rerollItem(playerSession.id(), itemId);
            response = Response.ok().build(); //TODO send back data with results
        } catch (PlayerInsufficientFundsException | MaxRerollReachedException | MarketItemDoesNotExistException |
                 PlayerNotFoundException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.CannotRerollItem).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    @GET
    @Path("/items")
    @Operation(summary = "Get market items", description = "Fetch all item data available for a player")
    public Response allItems(){
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            List<MarketItemResponse> itemList = marketService
                    .fetchAvailableItems(playerSession.id());
            response = Response.ok(itemList).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.CannotRetrieveItems).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }
}