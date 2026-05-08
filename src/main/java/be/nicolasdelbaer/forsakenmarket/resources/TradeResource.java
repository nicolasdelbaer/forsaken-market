package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.AlreadyBoughtException;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.BadItemOwnershipException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.TradeService;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
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

@RequestScoped
@Authenticated
@RolesAllowed("Merchant")
@Path("/trade")
@Tag(name = "Market", description = "Buy & reroll operations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TradeResource {
    private static final Logger log = LoggerFactory.getLogger(MarketResource.class);

    @Inject private TradeService tradeService;
    @Context private SecurityContext securityContext;

    @POST
    @Path("/buy/{id}")
    @Operation(summary = "Buy an item", description = "Buy an item from the market")
    public Response buyItem(@PathParam("id") Long itemId) {
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            tradeService.buyItem(playerSession.id(), itemId);
            response = Response.ok().build();
        } catch (PlayerInsufficientFundsException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.InsufficientFunds).build();
        } catch (AlreadyBoughtException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(),
                    BadResponseUtils.AlreadyBought).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
        } catch (CannotSellInactiveItemException | BadItemOwnershipException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), BadResponseUtils.CannotSellItem).build();
            log.warn(e.getMessage(), e);
        } catch (UndefinedMarketPriceException | PlayerNotFoundException | MarketPriceNotFoundException e) {
            response = Response.status(Response.Status.NOT_FOUND.getStatusCode(), BadResponseUtils.CannotSellItem).build();

        }
        return response;
    }
}
