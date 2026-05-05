package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.BadItemOwnershipException;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.CannotDiscardItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.CollectionItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.inventory.InventoryItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.InventoryService;
import be.nicolasdelbaer.forsakenmarket.services.TradeService;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/inventory")
@Authenticated
@RolesAllowed("Merchant")
@Tag(name = "Inventory", description = "Manage inventory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    private static final Logger log = LoggerFactory.getLogger(InventoryResource.class);
    @Inject private InventoryService inventoryService;
    @Inject private TradeService tradeService;
    @Context private SecurityContext securityContext;

    @POST
    @Path("/discard/{id}")
    @Operation(summary = "Discard an item", description = "Discard an item from their inventory")
    public Response discardItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            inventoryService.discardItem(playerSession.id(), itemId);
            response = Response.ok().build(); //TODO send back data with results
        } catch (CannotDiscardItemException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), BadResponseUtils.CannotDiscardItem).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    @POST
    @Path("/sell/{id}")
    @Operation(summary = "Sell an item", description = "Sell an item from their inventory")
    public Response sellItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            tradeService.sellItem(playerSession.id(), itemId);
            response = Response.ok().build(); //TODO send back data with results
        } catch (UndefinedMarketPriceException | PlayerNotFoundException | BadItemOwnershipException | CannotSellInactiveItemException | MarketPriceNotFoundException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), BadResponseUtils.CannotSellItem).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    @GET
    @Path("/items")
    @Operation(summary = "Get inventory items", description = "Fetch all item data available in a player's inventory")
    public Response getItems(){
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            List<InventoryItemResponse> itemList = inventoryService
                    .fetchItems(playerSession.id());
            response = Response.ok(itemList).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), BadResponseUtils.CannotRetrieveItems).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    @GET
    @Path("/collection")
    @Operation(summary = "Get collection items", description = "Fetch all blueprint item data available discovered by player")
    public Response getCollection(){
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            List<CollectionItemResponse> itemList = inventoryService
                    .fetchCollection(playerSession.id());
            response = Response.ok(itemList).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), BadResponseUtils.CannotRetrieveItems).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }
}
